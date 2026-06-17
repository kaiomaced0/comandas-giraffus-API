package k.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.GatewayConfigInputDTO;
import k.dto.GatewayConfigResponseDTO;
import k.dto.GatewayTesteResponseDTO;
import k.model.Empresa;
import k.model.EmpresaGatewayConfig;
import k.model.Usuario;
import k.repository.EmpresaGatewayConfigRepository;
import k.service.UsuarioLogadoService;

/**
 * Smoke tests do EmpresaGatewayConfigServiceImpl. Usa stubs (mesma justificativa
 * do MesaServiceTest: JDK 25 + Netty inviabilizam @QuarkusTest neste ambiente).
 */
class EmpresaGatewayConfigServiceTest {

    private EmpresaGatewayConfigServiceImpl service;
    private InMemoryRepository repository;
    private Empresa empresa;

    @BeforeEach
    void setup() {
        empresa = new Empresa();
        empresa.setNome("Empresa Gateway");
        empresa.setId(10L);

        Usuario usuarioFicticio = new Usuario();
        usuarioFicticio.setId(900L);
        usuarioFicticio.setLogin("teste-gateway");
        usuarioFicticio.setEmpresa(empresa);

        service = new EmpresaGatewayConfigServiceImpl();
        repository = new InMemoryRepository();
        service.repository = repository;
        service.usuarioLogadoService = new StubUsuarioLogadoService(usuarioFicticio);
    }

    @Test
    void insertAssociaAEmpresaDoLogado() {
        service.insert(new GatewayConfigInputDTO("ABACATE_PAY", "abc_chave_1234", "secreta", "SANDBOX", true));

        assertEquals(1, repository.store.size());
        EmpresaGatewayConfig salvo = repository.store.get(0);
        assertNotNull(salvo.getEmpresa());
        assertEquals(empresa.getId(), salvo.getEmpresa().getId());
    }

    @Test
    void respostaMascaraApiKeyENaoVazaSecret() {
        GatewayConfigResponseDTO resp = service.insert(
                new GatewayConfigInputDTO("asaas", "minha_api_key_ABCD", "segredo-super", "producao", true));

        assertEquals("ASAAS", resp.tipo());
        assertEquals("PRODUCAO", resp.ambiente());
        assertTrue(resp.habilitado());
        assertEquals("••••ABCD", resp.apiKeyMascarada());
        assertTrue(resp.temSecret());
        // O record nao possui campos de apiKey/apiSecret em texto puro.
        assertFalse(resp.toString().contains("minha_api_key_ABCD"));
        assertFalse(resp.toString().contains("segredo-super"));
    }

    @Test
    void apiKeyVaziaResultaEmTracoEsemSecret() {
        GatewayConfigResponseDTO resp = service.insert(
                new GatewayConfigInputDTO("ABACATE_PAY", "", null, "SANDBOX", false));

        assertEquals("—", resp.apiKeyMascarada());
        assertFalse(resp.temSecret());
        assertFalse(resp.habilitado());
    }

    @Test
    void tipoInvalidoDeve400() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.insert(new GatewayConfigInputDTO("PAYPAL", "k", "s", "SANDBOX", true)));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void ambienteInvalidoDeve400() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.insert(new GatewayConfigInputDTO("ASAAS", "k", "s", "LIVE", true)));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void testarSemApiKeyRetornaOkFalse() {
        GatewayConfigResponseDTO criado = service.insert(
                new GatewayConfigInputDTO("ABACATE_PAY", null, null, "SANDBOX", false));

        GatewayTesteResponseDTO teste = service.testar(criado.id());
        assertFalse(teste.ok());
        assertTrue(teste.emulado());
        assertNotNull(teste.mensagem());
    }

    @Test
    void testarComConfigCompletaRetornaOkEmulado() {
        GatewayConfigResponseDTO criado = service.insert(
                new GatewayConfigInputDTO("ASAAS", "chave-valida", "secreta", "PRODUCAO", true));

        GatewayTesteResponseDTO teste = service.testar(criado.id());
        assertTrue(teste.ok());
        assertTrue(teste.emulado());
        assertTrue(teste.mensagem().contains("Configuracao valida"));
    }

    @Test
    void getAllNaoRetornaConfigDeOutraEmpresa() {
        // Config da empresa logada
        service.insert(new GatewayConfigInputDTO("ABACATE_PAY", "minha-chave-aqui", "s", "SANDBOX", true));

        // Config de OUTRA empresa, injetada direto no store
        Empresa outra = new Empresa();
        outra.setId(99L);
        EmpresaGatewayConfig configOutra = new EmpresaGatewayConfig();
        configOutra.setEmpresa(outra);
        configOutra.setAtivo(true);
        repository.persist(configOutra);

        List<GatewayConfigResponseDTO> resp = service.getAll();
        assertEquals(1, resp.size(), "Deve retornar apenas a config da empresa do logado");
    }

    @Test
    void multiTenantTestarEmConfigDeOutraEmpresaDeve404() {
        Empresa outra = new Empresa();
        outra.setId(77L);
        EmpresaGatewayConfig configOutra = new EmpresaGatewayConfig();
        configOutra.setEmpresa(outra);
        configOutra.setAtivo(true);
        repository.persist(configOutra);

        assertThrows(jakarta.ws.rs.NotFoundException.class, () -> service.testar(configOutra.getId()));
    }

    private final class InMemoryRepository extends EmpresaGatewayConfigRepository {
        private final List<EmpresaGatewayConfig> store = new ArrayList<>();
        private long nextId = 1L;

        @Override
        public void persist(EmpresaGatewayConfig entity) {
            if (entity.getId() == null) {
                entity.setId(nextId++);
            }
            if (entity.getAtivo() == null) {
                entity.setAtivo(true);
            }
            store.add(entity);
        }

        @Override
        public EmpresaGatewayConfig findById(Long id) {
            return store.stream().filter(c -> id.equals(c.getId())).findFirst().orElse(null);
        }

        @Override
        public List<EmpresaGatewayConfig> findByEmpresa(Empresa e) {
            if (e == null) return List.of();
            List<EmpresaGatewayConfig> out = new ArrayList<>();
            for (EmpresaGatewayConfig c : store) {
                if (Boolean.TRUE.equals(c.getAtivo())
                        && c.getEmpresa() != null
                        && e.getId() != null
                        && e.getId().equals(c.getEmpresa().getId())) {
                    out.add(c);
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
