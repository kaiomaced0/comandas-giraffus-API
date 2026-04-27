package k.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.MovimentoCaixaInputDTO;
import k.dto.TransferenciaInputDTO;
import k.model.Caixa;
import k.model.Empresa;
import k.model.MovimentoCaixa;
import k.model.Usuario;
import k.repository.CaixaRepository;
import k.repository.MovimentoCaixaRepository;
import k.service.UsuarioLogadoService;

/**
 * Smoke tests do MovimentoCaixaServiceImpl. Usa stubs (mesma justificativa
 * dos demais: JDK 25 + Netty inviabilizam @QuarkusTest neste ambiente).
 */
class MovimentoCaixaServiceTest {

    private MovimentoCaixaServiceImpl service;
    private InMemoryMovimentoCaixaRepository repository;
    private InMemoryCaixaRepository caixaRepository;
    private Caixa caixaAberto;
    private Caixa caixaFechado;
    private Empresa empresa;

    @BeforeEach
    void setup() {
        empresa = new Empresa();
        empresa.setId(3L);

        caixaAberto = new Caixa();
        caixaAberto.setId(10L);
        caixaAberto.setNome("Aberto");
        caixaAberto.setFechado(false);
        caixaAberto.setAtivo(true);

        caixaFechado = new Caixa();
        caixaFechado.setId(11L);
        caixaFechado.setNome("Fechado");
        caixaFechado.setFechado(true);
        caixaFechado.setAtivo(true);

        List<Caixa> caixas = new ArrayList<>();
        caixas.add(caixaAberto);
        caixas.add(caixaFechado);
        empresa.setCaixas(caixas);

        Usuario usuarioFicticio = new Usuario();
        usuarioFicticio.setId(997L);
        usuarioFicticio.setLogin("teste-movcaixa");
        usuarioFicticio.setEmpresa(empresa);

        service = new MovimentoCaixaServiceImpl();
        repository = new InMemoryMovimentoCaixaRepository();
        caixaRepository = new InMemoryCaixaRepository();
        caixaRepository.add(caixaAberto);
        caixaRepository.add(caixaFechado);
        service.repository = repository;
        service.caixaRepository = caixaRepository;
        service.usuarioLogadoService = new StubUsuarioLogadoService(usuarioFicticio);
    }

    @Test
    void sangriaEmCaixaFechadoDeveErro() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.sangria(caixaFechado.getId(),
                        new MovimentoCaixaInputDTO(new BigDecimal("10.00"), "teste")));
        int status = ex.getResponse().getStatus();
        // Aceita 400 ou 422 conforme conveniencia da camada
        assertTrue(status == Response.Status.BAD_REQUEST.getStatusCode() || status == 422,
                "Esperado 400 ou 422, recebido " + status);
    }

    @Test
    void transferirMesmoCaixaOrigemDestinoDeveErro() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.transferir(caixaAberto.getId(),
                        new TransferenciaInputDTO(new BigDecimal("10.00"), "teste", caixaAberto.getId())));
        int status = ex.getResponse().getStatus();
        assertTrue(status == Response.Status.BAD_REQUEST.getStatusCode() || status == 422,
                "Esperado 400 ou 422, recebido " + status);
    }

    @Test
    void sangriaValidaValorPositivo() {
        WebApplicationException exZero = assertThrows(WebApplicationException.class,
                () -> service.sangria(caixaAberto.getId(),
                        new MovimentoCaixaInputDTO(BigDecimal.ZERO, "teste")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exZero.getResponse().getStatus());

        WebApplicationException exNeg = assertThrows(WebApplicationException.class,
                () -> service.sangria(caixaAberto.getId(),
                        new MovimentoCaixaInputDTO(new BigDecimal("-1.00"), "teste")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exNeg.getResponse().getStatus());

        WebApplicationException exNull = assertThrows(WebApplicationException.class,
                () -> service.sangria(caixaAberto.getId(),
                        new MovimentoCaixaInputDTO(null, "teste")));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), exNull.getResponse().getStatus());
    }

    private static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }

    private static final class InMemoryCaixaRepository extends CaixaRepository {
        private final List<Caixa> store = new ArrayList<>();

        void add(Caixa c) { store.add(c); }

        @Override
        public Caixa findById(Long id) {
            return store.stream().filter(c -> id.equals(c.getId())).findFirst().orElse(null);
        }
    }

    private static final class InMemoryMovimentoCaixaRepository extends MovimentoCaixaRepository {
        private long nextId = 1L;

        @Override
        public void persist(MovimentoCaixa entity) {
            entity.setId(nextId++);
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
