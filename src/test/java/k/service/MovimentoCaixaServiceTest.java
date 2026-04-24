package k.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import k.AbstractServiceTest;
import k.dto.AbrirCaixaDTO;
import k.dto.MovimentoCaixaDTO;
import k.exception.BusinessException;
import k.model.Caixa;
import k.model.enums.TipoMovimentoCaixa;
import k.repository.CaixaRepository;
import k.repository.MovimentoCaixaRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class MovimentoCaixaServiceTest extends AbstractServiceTest {

    @Inject
    CaixaService caixaService;

    @Inject
    MovimentoCaixaService service;

    @Inject
    CaixaRepository caixaRepository;

    @Inject
    MovimentoCaixaRepository movimentoCaixaRepository;

    @Test
    @Transactional
    public void sangria_caminhoFeliz_incluiNoCalculo() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("100.00"), null, null));
        Caixa cx = caixaRepository.findAbertoPorUsuario(caixa);
        service.sangria(cx.getId(), new MovimentoCaixaDTO(new BigDecimal("30.00"), "Despesa"));
        BigDecimal esperado = caixaService.calcularValorFechamentoEsperado(cx);
        // 100 - 30 = 70
        assertEquals(0, new BigDecimal("70.00").compareTo(esperado));
        assertEquals(1, movimentoCaixaRepository.findByCaixa(cx).stream()
                .filter(m -> m.getTipo() == TipoMovimentoCaixa.SANGRIA).count());
    }

    @Test
    @Transactional
    public void suprimento_caminhoFeliz_incluiNoCalculo() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("100.00"), null, null));
        Caixa cx = caixaRepository.findAbertoPorUsuario(caixa);
        service.suprimento(cx.getId(), new MovimentoCaixaDTO(new BigDecimal("50.00"), "Troco extra"));
        BigDecimal esperado = caixaService.calcularValorFechamentoEsperado(cx);
        // 100 + 50 = 150
        assertEquals(0, new BigDecimal("150.00").compareTo(esperado));
    }

    @Test
    @Transactional
    public void transferencia_debitaOrigemECreditaDestino() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("100.00"), null, null));
        Caixa origem = caixaRepository.findAbertoPorUsuario(caixa);
        // admin abre o próprio caixa para ser destino
        loginComo(admin);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("200.00"), null, null));
        Caixa destino = caixaRepository.findAbertoPorUsuario(admin);

        loginComo(caixa);
        service.transferir(origem.getId(), destino.getId(), new MovimentoCaixaDTO(new BigDecimal("40.00"), "Repasse"));

        BigDecimal esperadoOrigem = caixaService.calcularValorFechamentoEsperado(origem);
        BigDecimal esperadoDestino = caixaService.calcularValorFechamentoEsperado(destino);
        assertEquals(0, new BigDecimal("60.00").compareTo(esperadoOrigem)); // 100 - 40
        assertEquals(0, new BigDecimal("240.00").compareTo(esperadoDestino)); // 200 + 40
    }

    @Test
    @Transactional
    public void sangria_valorZero_falha() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("100.00"), null, null));
        Caixa cx = caixaRepository.findAbertoPorUsuario(caixa);
        assertThrows(BusinessException.class,
                () -> service.sangria(cx.getId(), new MovimentoCaixaDTO(BigDecimal.ZERO, "x")));
    }

    @Test
    @Transactional
    public void transferencia_caixaFechado_falha() {
        loginComo(caixa);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("100.00"), null, null));
        Caixa origem = caixaRepository.findAbertoPorUsuario(caixa);
        caixaService.fechar(new k.dto.FecharCaixaDTO(new BigDecimal("100.00"), null));
        loginComo(admin);
        caixaService.abrir(new AbrirCaixaDTO(new BigDecimal("200.00"), null, null));
        Caixa destino = caixaRepository.findAbertoPorUsuario(admin);
        assertThrows(BusinessException.class, () -> service.transferir(origem.getId(), destino.getId(),
                new MovimentoCaixaDTO(new BigDecimal("10"), "x")));
    }
}
