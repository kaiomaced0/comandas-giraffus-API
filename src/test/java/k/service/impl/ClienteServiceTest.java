package k.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.ClienteInputDTO;
import k.dto.ClienteResponseDTO;
import k.model.Cliente;
import k.model.Empresa;
import k.model.Usuario;
import k.repository.ClienteRepository;
import k.service.UsuarioLogadoService;

/**
 * Smoke tests do ClienteServiceImpl. Usa stubs (mesma justificativa do
 * MesaServiceTest: JDK 25 + Netty inviabilizam @QuarkusTest neste ambiente).
 */
class ClienteServiceTest {

    private ClienteServiceImpl service;
    private InMemoryClienteRepository repository;
    private Empresa empresa;

    @BeforeEach
    void setup() {
        empresa = new Empresa();
        empresa.setNome("Empresa Teste Cliente");
        empresa.setId(2L);

        Usuario usuarioFicticio = new Usuario();
        usuarioFicticio.setId(998L);
        usuarioFicticio.setLogin("teste-cliente");
        usuarioFicticio.setEmpresa(empresa);

        service = new ClienteServiceImpl();
        repository = new InMemoryClienteRepository();
        service.repository = repository;
        service.usuarioLogadoService = new StubUsuarioLogadoService(usuarioFicticio);
    }

    @Test
    void insertOrGetCriaNovo() {
        ClienteResponseDTO resp = service.insertOrGet(
                new ClienteInputDTO("12345678901", "Cliente Novo", "novo@x.com"));
        assertNotNull(resp);
        assertNotNull(resp.id());
        assertEquals("12345678901", resp.cpf());
        assertEquals("Cliente Novo", resp.nome());
        assertEquals("novo@x.com", resp.email());
    }

    @Test
    void insertOrGetMesmoCpfRetornaExistenteAtualizandoNomeEmail() {
        ClienteResponseDTO primeiro = service.insertOrGet(
                new ClienteInputDTO("98765432100", "Nome Original", "orig@x.com"));
        ClienteResponseDTO segundo = service.insertOrGet(
                new ClienteInputDTO("98765432100", "Nome Atualizado", "novo@x.com"));

        assertEquals(primeiro.id(), segundo.id(), "Mesmo CPF deve retornar mesmo cliente");
        assertEquals("Nome Atualizado", segundo.nome());
        assertEquals("novo@x.com", segundo.email());
    }

    @Test
    void cpfInvalidoDeve400() {
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.insertOrGet(new ClienteInputDTO("123", "X", null)));
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), ex.getResponse().getStatus());
    }

    private final class InMemoryClienteRepository extends ClienteRepository {
        private final List<Cliente> store = new ArrayList<>();
        private long nextId = 1L;

        @Override
        public void persist(Cliente entity) {
            entity.setId(nextId++);
            entity.setAtivo(true);
            store.add(entity);
        }

        @Override
        public Cliente findById(Long id) {
            return store.stream().filter(c -> id.equals(c.getId())).findFirst().orElse(null);
        }

        @Override
        public Optional<Cliente> findByEmpresaAndCpf(Empresa e, String cpf) {
            if (e == null || cpf == null || cpf.isBlank()) return Optional.empty();
            return store.stream()
                    .filter(c -> Boolean.TRUE.equals(c.getAtivo()))
                    .filter(c -> c.getEmpresa() != null && e.getId() != null
                            && e.getId().equals(c.getEmpresa().getId()))
                    .filter(c -> cpf.equals(c.getCpf()))
                    .findFirst();
        }

        @Override
        public List<Cliente> findByEmpresa(Empresa e) {
            if (e == null) return List.of();
            List<Cliente> out = new ArrayList<>();
            for (Cliente c : store) {
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
