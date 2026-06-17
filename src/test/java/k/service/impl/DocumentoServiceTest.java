package k.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.DocumentoResponseDTO;
import k.dto.DocumentoUrlDTO;
import k.model.Documento;
import k.model.Empresa;
import k.model.Usuario;
import k.repository.DocumentoRepository;
import k.service.UsuarioLogadoService;
import k.storage.StorageService;

/**
 * Smoke tests do DocumentoServiceImpl. Usa stubs em memoria (mesma
 * justificativa dos demais: JDK 25 + Netty inviabilizam @QuarkusTest neste
 * ambiente). Cobre validacao de upload e isolamento multi-tenant.
 */
class DocumentoServiceTest {

    private static final byte[] PNG = new byte[] { 1, 2, 3, 4 };

    private DocumentoServiceImpl service;
    private FakeStorageService storage;
    private InMemoryDocumentoRepository repository;
    private Empresa empresa;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        empresa = new Empresa();
        empresa.setId(7L);
        empresa.setNome("Empresa Doc");

        usuario = new Usuario();
        usuario.setId(900L);
        usuario.setLogin("teste-doc");
        usuario.setEmpresa(empresa);

        service = new DocumentoServiceImpl();
        storage = new FakeStorageService();
        repository = new InMemoryDocumentoRepository();
        service.storage = storage;
        service.repository = repository;
        service.usuarioLogadoService = new StubUsuarioLogadoService(usuario);
    }

    @Test
    void uploadRejeitaContentTypeForaDaAllowlist() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.upload("OUTRO", "x.exe", "application/octet-stream", PNG));
        assertEquals(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(),
                ex.getResponse().getStatus());
    }

    @Test
    void uploadRejeitaMaiorQue10Mb() {
        byte[] grande = new byte[(int) (DocumentoServiceImpl.MAX_TAMANHO_BYTES + 1)];
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.upload("OUTRO", "grande.pdf", "application/pdf", grande));
        assertEquals(Response.Status.REQUEST_ENTITY_TOO_LARGE.getStatusCode(),
                ex.getResponse().getStatus());
    }

    @Test
    void uploadRejeitaTipoInvalido() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.upload("NAO_EXISTE", "x.png", "image/png", PNG));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void uploadGeraChaveComPrefixoDaEmpresaEUuid() {
        DocumentoResponseDTO resp = service.upload("PRODUTO_IMAGEM", "Foto Produto.png", "image/png", PNG);
        assertNotNull(resp);
        assertNotNull(resp.id());
        assertEquals("image/png", resp.contentType());
        assertEquals(Long.valueOf(PNG.length), resp.tamanhoBytes());

        // A objectKey NUNCA vai no DTO; verificamos no storage/entidade.
        String key = storage.ultimaChave;
        assertNotNull(key);
        assertTrue(key.startsWith("empresa-7/PRODUTO_IMAGEM/"),
                "Chave deve ter prefixo do tenant e tipo: " + key);
        // nome sanitizado (espaco -> _), apos UUID-
        assertTrue(key.endsWith("-Foto_Produto.png"), "Nome sanitizado esperado no fim: " + key);
        // UUID + '-' presente entre o tipo e o nome
        String resto = key.substring("empresa-7/PRODUTO_IMAGEM/".length());
        assertTrue(resto.contains("-"), "Deve conter UUID-nome: " + resto);
    }

    @Test
    void listarRetornaSomenteDaEmpresaDoLogado() {
        // Documento da empresa do logado
        service.upload("PRODUTO_IMAGEM", "a.png", "image/png", PNG);
        // Documento de OUTRA empresa, injetado direto no repositorio
        Empresa outra = new Empresa();
        outra.setId(99L);
        Documento alheio = new Documento();
        alheio.setEmpresa(outra);
        alheio.setObjectKey("empresa-99/OUTRO/zzz-alheio.pdf");
        repository.persist(alheio);

        List<DocumentoResponseDTO> lista = service.listar();
        assertEquals(1, lista.size(), "Deve listar apenas o documento da empresa logada");
    }

    @Test
    void urlTemporariaDeOutraEmpresaDeve404() {
        Long idAlheio = inserirDocAlheio();
        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.urlTemporaria(idAlheio));
        assertNotNull(ex);
        // Nao deve ter gerado URL para objeto de outro tenant
        assertEquals(0, storage.presignedCount, "Nao deve gerar presigned para doc de outra empresa");
    }

    @Test
    void urlTemporariaUsaTtlCurto() {
        DocumentoResponseDTO resp = service.upload("FISCAL_PDF", "nota.pdf", "application/pdf", PNG);
        DocumentoUrlDTO url = service.urlTemporaria(resp.id());
        assertEquals(300, url.expiraEmSegundos());
        assertEquals(300, storage.ultimoTtl);
        assertNotNull(url.url());
    }

    @Test
    void excluirDeOutraEmpresaDeve404ENaoDeleta() {
        Long idAlheio = inserirDocAlheio();
        assertThrows(NotFoundException.class, () -> service.excluir(idAlheio));
        assertEquals(0, storage.deleteCount, "Nao deve remover objeto de outra empresa");
    }

    @Test
    void excluirDaPropriaEmpresaFazSoftDeleteERemoveDoStorage() {
        DocumentoResponseDTO resp = service.upload("RELATORIO", "rel.pdf", "application/pdf", PNG);
        service.excluir(resp.id());
        assertEquals(1, storage.deleteCount);
        Documento doc = repository.findById(resp.id());
        assertEquals(Boolean.FALSE, doc.getAtivo(), "Soft delete deve marcar ativo=false");
    }

    private Long inserirDocAlheio() {
        Empresa outra = new Empresa();
        outra.setId(99L);
        Documento alheio = new Documento();
        alheio.setEmpresa(outra);
        alheio.setObjectKey("empresa-99/OUTRO/zzz-alheio.pdf");
        repository.persist(alheio);
        return alheio.getId();
    }

    // ----- fakes -----

    private static final class FakeStorageService implements StorageService {
        private final Map<String, byte[]> store = new HashMap<>();
        String ultimaChave;
        int ultimoTtl;
        int presignedCount;
        int deleteCount;

        @Override
        public String upload(String objectKey, byte[] conteudo, String contentType) {
            store.put(objectKey, conteudo);
            ultimaChave = objectKey;
            return objectKey;
        }

        @Override
        public String presignedGet(String objectKey, int ttlSegundos) {
            presignedCount++;
            ultimoTtl = ttlSegundos;
            return "https://fake-presigned/" + objectKey + "?ttl=" + ttlSegundos;
        }

        @Override
        public void delete(String objectKey) {
            deleteCount++;
            store.remove(objectKey);
        }
    }

    private static final class InMemoryDocumentoRepository extends DocumentoRepository {
        private final List<Documento> store = new ArrayList<>();
        private long nextId = 1L;

        @Override
        public void persist(Documento entity) {
            if (entity.getId() == null) {
                entity.setId(nextId++);
            }
            if (entity.getAtivo() == null) {
                entity.setAtivo(true);
            }
            store.add(entity);
        }

        @Override
        public Documento findById(Long id) {
            return store.stream().filter(d -> id.equals(d.getId())).findFirst().orElse(null);
        }

        @Override
        public List<Documento> findByEmpresa(Empresa e) {
            if (e == null) {
                return List.of();
            }
            List<Documento> out = new ArrayList<>();
            for (Documento d : store) {
                if (Boolean.TRUE.equals(d.getAtivo())
                        && d.getEmpresa() != null
                        && e.getId() != null
                        && e.getId().equals(d.getEmpresa().getId())) {
                    out.add(d);
                }
            }
            return out;
        }
    }

    private static final class StubUsuarioLogadoService implements UsuarioLogadoService {
        private final Usuario usuario;

        StubUsuarioLogadoService(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Usuario getPerfilUsuarioLogado() {
            return usuario;
        }

        @Override
        public Response getPerfilUsuarioLogadoResponse() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Response updateSenha(k.dto.UsuarioLogadoSenhaDTO dto) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Response updateLogin(k.dto.UsuarioUpdateLoginDTO dto) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Response updateEmail(k.dto.UsuarioUpdateEmailDTO dto) {
            throw new UnsupportedOperationException();
        }
    }
}
