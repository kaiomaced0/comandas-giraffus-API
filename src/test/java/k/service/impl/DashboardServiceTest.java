package k.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;
import k.dto.DashboardKpisDTO;
import k.model.Comanda;
import k.model.Empresa;
import k.model.FormaPagamento;
import k.model.Pagamento;
import k.model.Usuario;
import k.repository.ComandaRepository;
import k.repository.PagamentoRepository;
import k.service.UsuarioLogadoService;

/**
 * Testes do DashboardServiceImpl. JUnit5 puro (sem @QuarkusTest — inviável neste
 * ambiente JDK25/Netty). Isola as queries via fakes que sobrescrevem
 * findParaRelatorio retornando listas em memória.
 */
class DashboardServiceTest {

    private DashboardServiceImpl service;
    private FakePagamentoRepository pagamentoRepository;
    private FakeComandaRepository comandaRepository;
    private Empresa empresa;

    @BeforeEach
    void setup() {
        empresa = new Empresa();
        empresa.setId(7L);

        Usuario logado = new Usuario();
        logado.setId(1L);
        logado.setEmpresa(empresa);

        service = new DashboardServiceImpl();
        pagamentoRepository = new FakePagamentoRepository();
        comandaRepository = new FakeComandaRepository();
        service.pagamentoRepository = pagamentoRepository;
        service.comandaRepository = comandaRepository;
        service.usuarioLogadoService = new StubUsuarioLogadoService(logado);
    }

    @Test
    void kpisAgregaFaturamentoGorjetasETicketMedio() {
        // 3 pagamentos: 100 + 50.50 + 49.50 = 200.00 ; gorjetas 5 + 0 + 2.5 = 7.50
        pagamentoRepository.lista.add(pagamento(new BigDecimal("100.00"), 5.0, FormaPagamento.PIX));
        pagamentoRepository.lista.add(pagamento(new BigDecimal("50.50"), 0.0, FormaPagamento.CREDITO));
        pagamentoRepository.lista.add(pagamento(new BigDecimal("49.50"), 2.5, FormaPagamento.AVISTA));

        // 2 comandas finalizadas, 1 aberta
        comandaRepository.lista.add(comanda(true));
        comandaRepository.lista.add(comanda(true));
        comandaRepository.lista.add(comanda(false));

        DashboardKpisDTO dto = service.kpis(null, null);

        assertEquals(0, new BigDecimal("200.00").compareTo(dto.faturamento()));
        assertEquals(3L, dto.numPagamentos());
        assertEquals(0, new BigDecimal("7.50").compareTo(dto.totalGorjetas()));
        assertEquals(1L, dto.comandasAbertas());
        assertEquals(2L, dto.comandasFinalizadas());
        // ticket medio = 200.00 / 2 = 100.00
        assertEquals(0, new BigDecimal("100.00").compareTo(dto.ticketMedio()));
    }

    @Test
    void ticketMedioZeroQuandoNenhumaComandaFinalizada() {
        pagamentoRepository.lista.add(pagamento(new BigDecimal("80.00"), 0.0, FormaPagamento.PIX));
        comandaRepository.lista.add(comanda(false));

        DashboardKpisDTO dto = service.kpis(null, null);

        assertEquals(0, new BigDecimal("80.00").compareTo(dto.faturamento()));
        assertEquals(1L, dto.numPagamentos());
        assertEquals(0L, dto.comandasFinalizadas());
        assertEquals(0, BigDecimal.ZERO.compareTo(dto.ticketMedio()));
    }

    @Test
    void semEmpresaRetornaZerado() {
        Usuario semEmpresa = new Usuario();
        semEmpresa.setId(2L);
        service.usuarioLogadoService = new StubUsuarioLogadoService(semEmpresa);

        DashboardKpisDTO dto = service.kpis(null, null);

        assertEquals(0L, dto.numPagamentos());
        assertEquals(0L, dto.comandasAbertas());
        assertEquals(0L, dto.comandasFinalizadas());
        assertEquals(0, BigDecimal.ZERO.compareTo(dto.faturamento()));
        assertEquals(0, BigDecimal.ZERO.compareTo(dto.totalGorjetas()));
        assertEquals(0, BigDecimal.ZERO.compareTo(dto.ticketMedio()));
    }

    @Test
    void faturamentoUsaFallbackValorPagamentoQuandoValorTotalNull() {
        Pagamento p = new Pagamento();
        p.setValorTotal(null);
        p.setValorPagamento(42.0);
        p.setValorGorjeta(0.0);
        p.setFormaPagamento(FormaPagamento.DEBITO);
        p.setDataInclusao(LocalDateTime.now());
        pagamentoRepository.lista.add(p);
        comandaRepository.lista.add(comanda(true));

        DashboardKpisDTO dto = service.kpis(null, null);

        assertEquals(0, new BigDecimal("42.00").compareTo(dto.faturamento()));
    }

    // ---------------- helpers ----------------

    private static Pagamento pagamento(BigDecimal valorTotal, Double gorjeta, FormaPagamento forma) {
        Pagamento p = new Pagamento();
        p.setValorTotal(valorTotal);
        p.setValorPagamento(valorTotal.doubleValue());
        p.setValorGorjeta(gorjeta);
        p.setFormaPagamento(forma);
        p.setDataInclusao(LocalDateTime.now());
        p.setEstornado(false);
        return p;
    }

    private static Comanda comanda(boolean finalizada) {
        Comanda c = new Comanda();
        c.setFinalizada(finalizada);
        c.setDataInclusao(LocalDateTime.now());
        return c;
    }

    private static final class FakePagamentoRepository extends PagamentoRepository {
        private final List<Pagamento> lista = new ArrayList<>();

        @Override
        public List<Pagamento> findParaRelatorio(Empresa empresa, LocalDateTime from, LocalDateTime to) {
            return lista;
        }
    }

    private static final class FakeComandaRepository extends ComandaRepository {
        private final List<Comanda> lista = new ArrayList<>();

        @Override
        public List<Comanda> findParaRelatorio(List<Long> idsEmpresa, LocalDateTime from, LocalDateTime to) {
            return lista;
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
