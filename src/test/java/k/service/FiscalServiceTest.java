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
import k.dto.DocumentoFiscalResponseDTO;
import k.dto.EmitirFiscalConsolidadoDTO;
import k.dto.EmitirFiscalDTO;
import k.exception.BusinessException;
import k.model.Comanda;
import k.model.FormaPagamento;
import k.model.Pagamento;
import k.model.enums.ModoPagamento;
import k.model.enums.StatusEmissaoFiscal;
import k.model.enums.TipoDocumentoFiscal;
import k.repository.ComandaRepository;
import k.repository.PagamentoRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class FiscalServiceTest extends AbstractServiceTest {

    @Inject
    FiscalService service;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    PagamentoRepository pagamentoRepository;

    @Test
    @Transactional
    public void emitirDaComanda_geraDocumentoEmulado() {
        Comanda c = novaComanda();
        Response r = service.emitirDaComanda(c.getId(), new EmitirFiscalDTO(null, TipoDocumentoFiscal.NFCE));
        assertEquals(200, r.getStatus());
        DocumentoFiscalResponseDTO d = (DocumentoFiscalResponseDTO) r.getEntity();
        assertTrue(d.emulado());
        assertEquals(StatusEmissaoFiscal.EMITIDO, d.statusEmissao());
        assertNotNull(d.numero());
    }

    @Test
    @Transactional
    public void emitirConsolidado_juntaNPagamentos() {
        Comanda c = novaComanda();
        Pagamento p1 = novoPagamento(c, new BigDecimal("10.00"));
        Pagamento p2 = novoPagamento(c, new BigDecimal("20.00"));
        List<Long> ids = new ArrayList<>();
        ids.add(p1.getId());
        ids.add(p2.getId());
        Response r = service.emitirConsolidado(new EmitirFiscalConsolidadoDTO(ids, null, TipoDocumentoFiscal.NFCE));
        assertEquals(200, r.getStatus());
        DocumentoFiscalResponseDTO d = (DocumentoFiscalResponseDTO) r.getEntity();
        assertEquals(2, d.pagamentosIds().size());
    }

    @Test
    @Transactional
    public void emitirConsolidado_listaVazia_falha() {
        assertThrows(BusinessException.class,
                () -> service.emitirConsolidado(new EmitirFiscalConsolidadoDTO(new ArrayList<>(), null, null)));
    }

    @Test
    @Transactional
    public void cancelar_marcaStatusCancelado() {
        Comanda c = novaComanda();
        Response r = service.emitirDaComanda(c.getId(), new EmitirFiscalDTO(null, null));
        DocumentoFiscalResponseDTO d = (DocumentoFiscalResponseDTO) r.getEntity();
        Response canc = service.cancelar(d.id());
        DocumentoFiscalResponseDTO after = (DocumentoFiscalResponseDTO) canc.getEntity();
        assertEquals(StatusEmissaoFiscal.CANCELADO, after.statusEmissao());
    }

    private Comanda novaComanda() {
        Comanda c = new Comanda();
        c.setNome("Comanda Fiscal");
        c.setPreco(50.0);
        c.setFinalizada(false);
        c.setAtendente(admin);
        comandaRepository.persist(c);
        return c;
    }

    private Pagamento novoPagamento(Comanda c, BigDecimal v) {
        Pagamento p = new Pagamento();
        p.setComanda(c);
        p.setFormaPagamento(FormaPagamento.PIX);
        p.setModo(ModoPagamento.SIMPLES);
        p.setUsuarioCaixa(admin);
        p.setValorTotal(v);
        p.setValorPagamento(v);
        p.setEstornado(false);
        p.setPagamentoRealizado(true);
        pagamentoRepository.persist(p);
        return p;
    }
}
