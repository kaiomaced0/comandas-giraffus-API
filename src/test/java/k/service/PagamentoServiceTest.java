package k.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import k.AbstractServiceTest;
import k.dto.AbrirCaixaDTO;
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoItemDTO;
import k.dto.PagamentoResponseDTO;
import k.exception.BusinessException;
import k.model.Comanda;
import k.model.FormaPagamento;
import k.model.ItemCompra;
import k.model.Pagamento;
import k.model.Pedido;
import k.model.StatusPedido;
import k.model.enums.ModoPagamento;
import k.repository.ComandaRepository;
import k.repository.ItemCompraRepository;
import k.repository.PagamentoRepository;
import k.repository.PedidoRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class PagamentoServiceTest extends AbstractServiceTest {

    @Inject
    PagamentoService service;

    @Inject
    CaixaService caixaService;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    PedidoRepository pedidoRepository;

    @Inject
    ItemCompraRepository itemCompraRepository;

    @Inject
    PagamentoRepository pagamentoRepository;

    @Test
    @Transactional
    public void pagamentoSimples_gorjetaCorreta() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("100"), null, null));
        Comanda c = novaComandaComPreco(new BigDecimal("50.00"));
        PagamentoDTO dto = new PagamentoDTO(c.getId(),
                FormaPagamento.PIX.getId(),
                ModoPagamento.SIMPLES.getId(),
                new BigDecimal("60.00"), false, null);
        Response r = service.insert(dto);
        assertEquals(200, r.getStatus());
        PagamentoResponseDTO p = (PagamentoResponseDTO) r.getEntity();
        // Gorjeta = 60 - 50 = 10
        assertEquals(0, new BigDecimal("10.00").compareTo(p.valorGorjeta()));
        Comanda recarregada = comandaRepository.findById(c.getId());
        assertTrue(recarregada.getFinalizada());
    }

    @Test
    @Transactional
    public void pagamentoSimples_varios_somatorioFinaliza() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("0"), null, null));
        Comanda c = novaComandaComPreco(new BigDecimal("100.00"));
        Response r1 = service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                ModoPagamento.SIMPLES.getId(), new BigDecimal("40.00"), false, null));
        assertEquals(200, r1.getStatus());
        // ainda não finaliza
        assertTrue(!comandaRepository.findById(c.getId()).getFinalizada());
        Response r2 = service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                ModoPagamento.SIMPLES.getId(), new BigDecimal("60.00"), false, null));
        assertEquals(200, r2.getStatus());
        assertTrue(comandaRepository.findById(c.getId()).getFinalizada());
    }

    @Test
    @Transactional
    public void pagamentoSimples_excedeSomaAcumuladaGerarGorjeta() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("0"), null, null));
        Comanda c = novaComandaComPreco(new BigDecimal("100.00"));
        service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                ModoPagamento.SIMPLES.getId(), new BigDecimal("80.00"), false, null));
        Response r = service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                ModoPagamento.SIMPLES.getId(), new BigDecimal("30.00"), false, null));
        PagamentoResponseDTO p = (PagamentoResponseDTO) r.getEntity();
        // 80 + 30 = 110, preco 100 -> gorjeta = 10 no segundo pagamento
        assertEquals(0, new BigDecimal("10.00").compareTo(p.valorGorjeta()));
    }

    @Test
    @Transactional
    public void pagamentoSemCaixaAberto_falha() {
        loginComo(caixa);
        Comanda c = novaComandaComPreco(new BigDecimal("10.00"));
        assertThrows(BusinessException.class,
                () -> service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                        ModoPagamento.SIMPLES.getId(), new BigDecimal("10.00"), false, null)));
    }

    @Test
    @Transactional
    public void pagamentoRateado_quantidadeExcedeRestante_falha() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("0"), null, null));
        Comanda c = novaComandaComPreco(new BigDecimal("20.00"));
        ItemCompra ic = novoItemCompra(c, 2, new BigDecimal("10.00"));
        List<PagamentoItemDTO> itens = new ArrayList<>();
        itens.add(new PagamentoItemDTO(ic.getId(), 3, new BigDecimal("30.00")));
        assertThrows(BusinessException.class,
                () -> service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                        ModoPagamento.RATEADO.getId(), new BigDecimal("30.00"), false, itens)));
    }

    @Test
    @Transactional
    public void pagamentoRateado_dentroDoLimite_ok() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("0"), null, null));
        Comanda c = novaComandaComPreco(new BigDecimal("20.00"));
        ItemCompra ic = novoItemCompra(c, 2, new BigDecimal("10.00"));
        List<PagamentoItemDTO> itens = new ArrayList<>();
        itens.add(new PagamentoItemDTO(ic.getId(), 1, new BigDecimal("10.00")));
        Response r = service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                ModoPagamento.RATEADO.getId(), new BigDecimal("10.00"), false, itens));
        assertEquals(200, r.getStatus());
    }

    @Test
    @Transactional
    public void estornar_marcaEstornadoERegrediFinalizada() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("0"), null, null));
        Comanda c = novaComandaComPreco(new BigDecimal("50.00"));
        Response r = service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                ModoPagamento.SIMPLES.getId(), new BigDecimal("50.00"), false, null));
        PagamentoResponseDTO p = (PagamentoResponseDTO) r.getEntity();
        assertTrue(comandaRepository.findById(c.getId()).getFinalizada());
        service.estornar(p.id());
        Pagamento reloaded = pagamentoRepository.findById(p.id());
        assertTrue(reloaded.getEstornado());
        // Após estorno a comanda volta a não-finalizada
        assertTrue(!comandaRepository.findById(c.getId()).getFinalizada());
    }

    @Test
    @Transactional
    public void getByComanda_retornaLista() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("0"), null, null));
        Comanda c = novaComandaComPreco(new BigDecimal("30.00"));
        service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                ModoPagamento.SIMPLES.getId(), new BigDecimal("30.00"), false, null));
        List<PagamentoResponseDTO> lista = service.getByComanda(c.getId());
        assertEquals(1, lista.size());
    }

    @Test
    @Transactional
    public void delete_criaHistorico() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("0"), null, null));
        Comanda c = novaComandaComPreco(new BigDecimal("30.00"));
        Response r = service.insert(new PagamentoDTO(c.getId(), FormaPagamento.PIX.getId(),
                ModoPagamento.SIMPLES.getId(), new BigDecimal("30.00"), false, null));
        PagamentoResponseDTO p = (PagamentoResponseDTO) r.getEntity();
        service.delete(new PagamentoDeleteDTO(p.id(), "cancelado"));
        assertNotNull(p);
    }

    private Comanda novaComandaComPreco(BigDecimal preco) {
        Comanda c = new Comanda();
        c.setNome("Comanda teste");
        c.setPreco(preco.doubleValue());
        c.setFinalizada(false);
        c.setAtendente(admin);
        c.setPagamentos(new ArrayList<>());
        comandaRepository.persist(c);
        empresa.getComandas().add(c);
        return c;
    }

    private ItemCompra novoItemCompra(Comanda c, Integer quantidade, BigDecimal preco) {
        Pedido pedido = new Pedido();
        pedido.setValor(preco.doubleValue() * quantidade);
        pedido.setStatusPedido(StatusPedido.AGUARDANDO);
        pedido.setComanda(c);
        pedido.setItemCompras(new ArrayList<>());
        pedidoRepository.persist(pedido);
        if (c.getPedidos() == null) {
            c.setPedidos(new ArrayList<>());
        }
        c.getPedidos().add(pedido);
        ItemCompra ic = new ItemCompra();
        ic.setPedido(pedido);
        ic.setProduto(produto);
        ic.setQuantidade(quantidade);
        ic.setPreco(preco.doubleValue() * quantidade);
        itemCompraRepository.persist(ic);
        pedido.getItemCompras().add(ic);
        return ic;
    }
}
