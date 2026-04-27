package k.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;
import k.dto.PagamentoDTO;
import k.model.Caixa;
import k.model.Comanda;
import k.model.Empresa;
import k.model.Pagamento;
import k.model.Usuario;
import k.repository.ComandaRepository;
import k.repository.PagamentoRemovidoHistoricoRepository;
import k.repository.PagamentoRepository;
import k.repository.UsuarioRepository;
import k.service.UsuarioLogadoService;

class PagamentoServiceImplTest {

    @Test
    void insertDeveCalcularGorjetaComoValorPagoMenosPreco() {
        PagamentoServiceImpl service = new PagamentoServiceImpl();

        Comanda comanda = new Comanda();
        comanda.setId(10L);
        comanda.setPreco(100.0);
        comanda.setFinalizada(false);

        Empresa empresa = new Empresa();
        empresa.setCaixaAtual(new Caixa());

        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(5L);
        usuarioLogado.setEmpresa(empresa);

        CapturingPagamentoRepository pagamentoRepository = new CapturingPagamentoRepository();
        service.repository = pagamentoRepository;
        service.comandaRepository = new StubComandaRepository(comanda);
        service.usuarioRepository = new StubUsuarioRepository(usuarioLogado);
        service.usuarioLogadoService = new StubUsuarioLogadoService(usuarioLogado);
        service.pagamentoRemovidoHistoricoRepository = new PagamentoRemovidoHistoricoRepository();

        Response response = service.insert(new PagamentoDTO(10L, 3, 115.0, false));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(pagamentoRepository.persisted);
        assertEquals(15.0, pagamentoRepository.persisted.getValorGorjeta());
        assertEquals(true, pagamentoRepository.persisted.getPagamentoRealizado());
        assertEquals(true, comanda.getFinalizada());
    }

    private static final class CapturingPagamentoRepository extends PagamentoRepository {
        private Pagamento persisted;

        @Override
        public void persist(Pagamento entity) {
            this.persisted = entity;
        }
    }

    private static final class StubComandaRepository extends ComandaRepository {
        private final Comanda comanda;

        private StubComandaRepository(Comanda comanda) {
            this.comanda = comanda;
        }

        @Override
        public Comanda findById(Long id) {
            return comanda;
        }
    }

    private static final class StubUsuarioRepository extends UsuarioRepository {
        private final Usuario usuario;

        private StubUsuarioRepository(Usuario usuario) {
            this.usuario = usuario;
        }

        @Override
        public Usuario findById(Long id) {
            return usuario;
        }
    }

    private static final class StubUsuarioLogadoService implements UsuarioLogadoService {
        private final Usuario usuario;

        private StubUsuarioLogadoService(Usuario usuario) {
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
        public Response updateSenha(k.dto.UsuarioLogadoSenhaDTO usuarioLogadoSenhaDTO) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Response updateLogin(k.dto.UsuarioUpdateLoginDTO usuarioUpdateSenha) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Response updateEmail(k.dto.UsuarioUpdateEmailDTO usuarioUpdateSenha) {
            throw new UnsupportedOperationException();
        }
    }
}
