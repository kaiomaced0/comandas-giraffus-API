package k.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.ws.rs.core.Response;
import k.dto.FaturamentoPorFormaDTO;
import k.dto.TopProdutoDTO;
import k.model.Comanda;
import k.model.Empresa;
import k.model.FormaPagamento;
import k.model.ItemCompra;
import k.model.Pagamento;
import k.model.Produto;
import k.model.Usuario;
import k.repository.ComandaRepository;
import k.repository.ItemCompraRepository;
import k.repository.PagamentoRepository;
import k.service.UsuarioLogadoService;

/**
 * Testes do RelatorioServiceImpl (forma-pagamento e top-produtos). JUnit5 puro,
 * fakes em memória sobrescrevendo findParaRelatorio.
 */
class RelatorioServiceTest {

    private RelatorioServiceImpl service;
    private FakePagamentoRepository pagamentoRepository;
    private FakeItemCompraRepository itemCompraRepository;
    private Empresa empresa;

    @BeforeEach
    void setup() {
        empresa = new Empresa();
        empresa.setId(7L);
        // duas comandas associadas à empresa (para derivar ids no top-produtos)
        Comanda c1 = new Comanda();
        c1.setId(100L);
        Comanda c2 = new Comanda();
        c2.setId(200L);
        List<Comanda> comandas = new ArrayList<>();
        comandas.add(c1);
        comandas.add(c2);
        empresa.setComandas(comandas);

        Usuario logado = new Usuario();
        logado.setId(1L);
        logado.setEmpresa(empresa);

        service = new RelatorioServiceImpl();
        pagamentoRepository = new FakePagamentoRepository();
        itemCompraRepository = new FakeItemCompraRepository();
        service.pagamentoRepository = pagamentoRepository;
        service.itemCompraRepository = itemCompraRepository;
        service.comandaRepository = new ComandaRepository();
        service.usuarioLogadoService = new StubUsuarioLogadoService(logado);
    }

    @Test
    void faturamentoPorFormaAgrupaPorFormaEOrdenaPorId() {
        pagamentoRepository.lista.add(pagamento(new BigDecimal("100.00"), FormaPagamento.PIX));
        pagamentoRepository.lista.add(pagamento(new BigDecimal("50.00"), FormaPagamento.PIX));
        pagamentoRepository.lista.add(pagamento(new BigDecimal("30.00"), FormaPagamento.CREDITO));

        List<FaturamentoPorFormaDTO> r = service.faturamentoPorForma(null, null);

        assertEquals(2, r.size());
        // ordenado por id: CREDITO(1) antes de PIX(3)
        FaturamentoPorFormaDTO credito = r.get(0);
        assertEquals(Integer.valueOf(1), credito.formaPagamentoId());
        assertEquals("Cartao Credito", credito.formaPagamentoLabel());
        assertEquals(1L, credito.quantidade());
        assertEquals(0, new BigDecimal("30.00").compareTo(credito.total()));

        FaturamentoPorFormaDTO pix = r.get(1);
        assertEquals(Integer.valueOf(3), pix.formaPagamentoId());
        assertEquals("Pix", pix.formaPagamentoLabel());
        assertEquals(2L, pix.quantidade());
        assertEquals(0, new BigDecimal("150.00").compareTo(pix.total()));
    }

    @Test
    void topProdutosAgrupaSomaQuantidadeReceitaOrdenaEAplicaLimite() {
        Produto coca = produto(1L, "Coca");
        Produto agua = produto(2L, "Agua");
        Produto suco = produto(3L, "Suco");

        // Coca: 2x + 3x = 5 ; preco 5.00 -> receita 25.00
        itemCompraRepository.lista.add(item(coca, 2, 5.0));
        itemCompraRepository.lista.add(item(coca, 3, 5.0));
        // Agua: 10x ; preco 3.00 -> receita 30.00
        itemCompraRepository.lista.add(item(agua, 10, 3.0));
        // Suco: 1x ; preco 8.00 -> receita 8.00
        itemCompraRepository.lista.add(item(suco, 1, 8.0));

        List<TopProdutoDTO> r = service.topProdutos(null, null, 2);

        // ordenado desc por quantidade: Agua(10), Coca(5) ; limite 2 corta Suco
        assertEquals(2, r.size());
        assertEquals(Long.valueOf(2L), r.get(0).produtoId());
        assertEquals("Agua", r.get(0).nome());
        assertEquals(10L, r.get(0).quantidade());
        assertEquals(0, new BigDecimal("30.00").compareTo(r.get(0).receita()));

        assertEquals(Long.valueOf(1L), r.get(1).produtoId());
        assertEquals("Coca", r.get(1).nome());
        assertEquals(5L, r.get(1).quantidade());
        assertEquals(0, new BigDecimal("25.00").compareTo(r.get(1).receita()));
    }

    @Test
    void semEmpresaRetornaVazio() {
        Usuario semEmpresa = new Usuario();
        semEmpresa.setId(2L);
        service.usuarioLogadoService = new StubUsuarioLogadoService(semEmpresa);

        assertEquals(0, service.faturamentoPorForma(null, null).size());
        assertEquals(0, service.topProdutos(null, null, 10).size());
    }

    // ---------------- helpers ----------------

    private static Pagamento pagamento(BigDecimal valorTotal, FormaPagamento forma) {
        Pagamento p = new Pagamento();
        p.setValorTotal(valorTotal);
        p.setValorPagamento(valorTotal.doubleValue());
        p.setFormaPagamento(forma);
        p.setDataInclusao(LocalDateTime.now());
        p.setEstornado(false);
        return p;
    }

    private static Produto produto(Long id, String nome) {
        Produto p = new Produto();
        p.setId(id);
        p.setNome(nome);
        return p;
    }

    private static ItemCompra item(Produto produto, int quantidade, double preco) {
        ItemCompra ic = new ItemCompra();
        ic.setProduto(produto);
        ic.setQuantidade(quantidade);
        ic.setPreco(preco);
        ic.setDataInclusao(LocalDateTime.now());
        return ic;
    }

    private static final class FakePagamentoRepository extends PagamentoRepository {
        private final List<Pagamento> lista = new ArrayList<>();

        @Override
        public List<Pagamento> findParaRelatorio(Empresa empresa, LocalDateTime from, LocalDateTime to) {
            return lista;
        }
    }

    private static final class FakeItemCompraRepository extends ItemCompraRepository {
        private final List<ItemCompra> lista = new ArrayList<>();

        @Override
        public List<ItemCompra> findParaRelatorio(List<Long> idsComanda, LocalDateTime from, LocalDateTime to) {
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
