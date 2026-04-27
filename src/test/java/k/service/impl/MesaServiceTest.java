package k.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.MesaInputDTO;
import k.dto.MesaResponseDTO;
import k.model.Empresa;
import k.model.Mesa;
import k.model.Usuario;
import k.repository.MesaRepository;
import k.service.UsuarioLogadoService;

/**
 * Smoke tests do MesaServiceImpl. Usa stubs simples (mesmo padrao do
 * PagamentoServiceImplTest existente) para evitar dependencia de @QuarkusTest:
 * JDK 25 + Netty no Windows quebra o boot do Quarkus em forks de teste com
 * "Unable to establish loopback connection" em sun.nio.ch.UnixDomainSockets.connect0,
 * inviabilizando @QuarkusTest neste ambiente.
 */
class MesaServiceTest {

    private MesaServiceImpl service;
    private InMemoryMesaRepository repository;
    private Empresa empresa;

    @BeforeEach
    void setup() {
        empresa = new Empresa();
        empresa.setNome("Empresa Teste");
        empresa.setId(1L);

        Usuario usuarioFicticio = new Usuario();
        usuarioFicticio.setId(999L);
        usuarioFicticio.setLogin("teste");
        usuarioFicticio.setEmpresa(empresa);

        service = new MesaServiceImpl();
        repository = new InMemoryMesaRepository();
        service.repository = repository;
        service.usuarioLogadoService = new StubUsuarioLogadoService(usuarioFicticio);
    }

    @Test
    void insertCriaMesa() {
        MesaResponseDTO resp = service.insert(new MesaInputDTO("Mesa-A", 4));
        assertNotNull(resp);
        assertNotNull(resp.id());
        assertEquals("Mesa-A", resp.identificador());
        assertEquals(4, resp.capacidade());
    }

    @Test
    void insertSegundoComMesmoIdentificadorDeve409() {
        service.insert(new MesaInputDTO("Mesa-Dup", 2));
        WebApplicationException ex = assertThrows(WebApplicationException.class,
                () -> service.insert(new MesaInputDTO("Mesa-Dup", 2)));
        assertEquals(Response.Status.CONFLICT.getStatusCode(), ex.getResponse().getStatus());
    }

    @Test
    void getByIdInexistenteDeve404() {
        assertThrows(NotFoundException.class, () -> service.getById(987654321L));
    }

    private final class InMemoryMesaRepository extends MesaRepository {
        private final List<Mesa> store = new ArrayList<>();
        private long nextId = 1L;

        @Override
        public void persist(Mesa entity) {
            entity.setId(nextId++);
            entity.setAtivo(true);
            store.add(entity);
        }

        @Override
        public Mesa findById(Long id) {
            return store.stream().filter(m -> id.equals(m.getId())).findFirst().orElse(null);
        }

        @Override
        public List<Mesa> findByEmpresa(Empresa e) {
            if (e == null) return List.of();
            List<Mesa> out = new ArrayList<>();
            for (Mesa m : store) {
                if (Boolean.TRUE.equals(m.getAtivo())
                        && m.getEmpresa() != null
                        && e.getId() != null
                        && e.getId().equals(m.getEmpresa().getId())) {
                    out.add(m);
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
