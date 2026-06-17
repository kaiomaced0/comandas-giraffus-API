package k.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.DocumentoResponseDTO;
import k.dto.DocumentoUrlDTO;
import k.model.Documento;
import k.model.Empresa;
import k.model.TipoDocumento;
import k.model.Usuario;
import k.repository.DocumentoRepository;
import k.service.DocumentoService;
import k.service.UsuarioLogadoService;
import k.storage.StorageService;

/**
 * Servico de documentos. A API e o unico broker de storage:
 * - toda escrita passa por aqui (autenticada);
 * - download somente via URL pre-assinada de TTL curto (300s);
 * - isolamento multi-tenant por prefixo de chave {@code empresa-{id}/...} e
 *   validacao de posse em todo acesso (presigned/delete) -> 404 se de outra
 *   empresa, sem vazar existencia.
 */
@ApplicationScoped
public class DocumentoServiceImpl implements DocumentoService {

    public static final Logger LOG = Logger.getLogger(DocumentoServiceImpl.class);

    /** Download temporario: TTL curto, conforme requisito de seguranca. */
    static final int PRESIGNED_TTL_SEGUNDOS = 300;

    /** Limite de tamanho de upload: 10 MB. */
    static final long MAX_TAMANHO_BYTES = 10L * 1024 * 1024;

    /** Allowlist de content-types aceitos. */
    static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp",
            "application/pdf");

    @Inject
    StorageService storage;

    @Inject
    DocumentoRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    @Transactional
    public DocumentoResponseDTO upload(String tipoStr, String nomeOriginal, String contentType, byte[] bytes) {
        Empresa emp = empresaLogada();

        // Conteudo obrigatorio
        if (bytes == null || bytes.length == 0) {
            throw new WebApplicationException("Arquivo obrigatorio", Response.Status.BAD_REQUEST);
        }
        // Limite de tamanho (<= 10 MB)
        if (bytes.length > MAX_TAMANHO_BYTES) {
            throw new WebApplicationException(
                    "Arquivo excede o limite de 10 MB",
                    Response.Status.REQUEST_ENTITY_TOO_LARGE);
        }
        // Allowlist de content-type
        String ct = contentType == null ? "" : contentType.trim().toLowerCase();
        if (!CONTENT_TYPES_PERMITIDOS.contains(ct)) {
            throw new WebApplicationException(
                    "Tipo de arquivo nao permitido: " + (contentType == null ? "<vazio>" : contentType),
                    Response.Status.UNSUPPORTED_MEDIA_TYPE);
        }
        // Tipo de documento (enum) -> 400 se invalido
        TipoDocumento tipo = parseTipo(tipoStr);
        // Sanitiza nome (remove path/.. e caracteres perigosos)
        String nomeSanitizado = sanitizeNome(nomeOriginal);
        // Checksum SHA-256 para integridade/auditoria
        String checksum = sha256Hex(bytes);

        // Chave SEMPRE com prefixo do tenant e UUID (nunca o nome do cliente como chave)
        String objectKey = "empresa-" + emp.getId() + "/" + tipo.name() + "/"
                + UUID.randomUUID() + "-" + nomeSanitizado;

        storage.upload(objectKey, bytes, ct);

        Documento doc = new Documento();
        doc.setEmpresa(emp);
        doc.setTipo(tipo);
        doc.setObjectKey(objectKey);
        doc.setContentType(ct);
        doc.setTamanhoBytes((long) bytes.length);
        doc.setNomeOriginal(nomeSanitizado);
        doc.setChecksumSha256(checksum);
        doc.setCriadoPor(usuarioLogadoService.getPerfilUsuarioLogado());
        repository.persist(doc);

        LOG.infof("Documento criado id=%d empresa=%d tipo=%s", doc.getId(), emp.getId(), tipo.name());
        return toResponse(doc);
    }

    @Override
    public List<DocumentoResponseDTO> listar() {
        Empresa emp = empresaLogada();
        return repository.findByEmpresa(emp).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentoUrlDTO urlTemporaria(Long id) {
        Documento doc = findOwned(id);
        String url = storage.presignedGet(doc.getObjectKey(), PRESIGNED_TTL_SEGUNDOS);
        return new DocumentoUrlDTO(url, PRESIGNED_TTL_SEGUNDOS);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Documento doc = findOwned(id);
        storage.delete(doc.getObjectKey());
        doc.setAtivo(false);
        LOG.infof("Documento excluido (soft delete) id=%d", doc.getId());
    }

    /**
     * Busca o documento e valida a posse pela empresa do usuario logado.
     * Retorna 404 (NotFoundException) tanto para inexistente quanto para
     * documento de outra empresa -> nao vaza existencia entre tenants.
     */
    private Documento findOwned(Long id) {
        Documento doc = repository.findById(id);
        if (doc == null || !Boolean.TRUE.equals(doc.getAtivo())) {
            throw new NotFoundException("Documento nao encontrado");
        }
        Empresa emp = empresaLogada();
        if (doc.getEmpresa() == null || doc.getEmpresa().getId() == null
                || !doc.getEmpresa().getId().equals(emp.getId())) {
            throw new NotFoundException("Documento nao encontrado");
        }
        return doc;
    }

    private Empresa empresaLogada() {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        Empresa emp = logado == null ? null : logado.getEmpresa();
        if (emp == null || emp.getId() == null) {
            throw new WebApplicationException(
                    "Usuario logado sem empresa associada",
                    Response.Status.FORBIDDEN);
        }
        return emp;
    }

    private TipoDocumento parseTipo(String tipoStr) {
        if (tipoStr == null || tipoStr.isBlank()) {
            throw new WebApplicationException("Tipo de documento obrigatorio", Response.Status.BAD_REQUEST);
        }
        try {
            return TipoDocumento.valueOf(tipoStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(
                    "Tipo de documento invalido: " + tipoStr,
                    Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Sanitiza o nome de arquivo: descarta qualquer componente de caminho
     * (path traversal), remove caracteres perigosos e limita o tamanho.
     * Nunca usado como chave de storage por si so (a chave usa UUID).
     */
    static String sanitizeNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return "arquivo";
        }
        String base = nome.trim();
        // Remove qualquer prefixo de diretorio (Windows e Unix)
        int barra = Math.max(base.lastIndexOf('/'), base.lastIndexOf('\\'));
        if (barra >= 0) {
            base = base.substring(barra + 1);
        }
        // Neutraliza ".." e mantem apenas caracteres seguros
        base = base.replace("..", "");
        base = base.replaceAll("[^a-zA-Z0-9._-]", "_");
        // Remove pontos/sublinhados/hifens iniciais (evita nomes ocultos/vazios)
        base = base.replaceFirst("^[._-]+", "");
        if (base.isBlank()) {
            return "arquivo";
        }
        if (base.length() > 120) {
            base = base.substring(0, 120);
        }
        return base;
    }

    private static String sha256Hex(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(bytes);
            StringBuilder sb = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao calcular checksum SHA-256", e);
        }
    }

    private DocumentoResponseDTO toResponse(Documento d) {
        return new DocumentoResponseDTO(
                d.getId(),
                d.getTipo() == null ? null : d.getTipo().name(),
                d.getContentType(),
                d.getTamanhoBytes(),
                d.getNomeOriginal(),
                d.getDataInclusao());
    }
}
