package k.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import k.AbstractServiceTest;
import k.dto.AbrirCaixaDTO;
import k.dto.CaixaResponseDTO;
import k.dto.FecharCaixaDTO;
import k.dto.FecharForcadoDTO;
import k.exception.BusinessException;
import k.model.Caixa;
import k.repository.CaixaRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CaixaServiceTest extends AbstractServiceTest {

    @Inject
    CaixaService service;

    @Inject
    CaixaRepository caixaRepository;

    @Test
    @Transactional
    public void abrirCaixa_caminhoFeliz() {
        loginComo(caixa);
        service.abrir(new AbrirCaixaDTO(new BigDecimal("100.00"), "Caixa 1", null));
        CaixaResponseDTO meu = service.getCaixaMeu();
        assertNotNull(meu);
        assertEquals(0, new BigDecimal("100.00").compareTo(meu.valorAbertura()));
        assertFalse(meu.fechado());
    }

    @Test
    @Transactional
    public void abrirCaixa_bloqueiaSeJaTemAberto() {
        loginComo(caixa);
        service.abrir(new AbrirCaixaDTO(new BigDecimal("50.00"), null, null));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.abrir(new AbrirCaixaDTO(new BigDecimal("10.00"), null, null)));
        assertTrue(ex.getMessage().contains("já possui caixa aberto"));
    }

    @Test
    @Transactional
    public void abrirCaixa_semValorAbertura_falha() {
        loginComo(caixa);
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.abrir(new AbrirCaixaDTO(null, null, null)));
        assertTrue(ex.getMessage().contains("valorAbertura"));
    }

    @Test
    @Transactional
    public void fecharCaixa_semDiferenca_ok() {
        loginComo(caixa);
        service.abrir(new AbrirCaixaDTO(new BigDecimal("200.00"), null, null));
        service.fechar(new FecharCaixaDTO(new BigDecimal("200.00"), null));
        CaixaResponseDTO meu = service.getCaixaMeu();
        // Não há mais caixa aberto para o usuário
        assertNull(meu);
    }

    @Test
    @Transactional
    public void fecharCaixa_comDiferenca_exigeObservacao() {
        loginComo(caixa);
        service.abrir(new AbrirCaixaDTO(new BigDecimal("200.00"), null, null));
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.fechar(new FecharCaixaDTO(new BigDecimal("180.00"), null)));
        assertTrue(ex.getMessage().contains("observacoesFechamento"));
    }

    @Test
    @Transactional
    public void fecharCaixa_comDiferencaEObservacao_persisteDiferenca() {
        loginComo(caixa);
        service.abrir(new AbrirCaixaDTO(new BigDecimal("200.00"), null, null));
        Caixa aberto = caixaRepository.findAbertoPorUsuario(caixa);
        Long id = aberto.getId();
        service.fechar(new FecharCaixaDTO(new BigDecimal("180.00"), "quebra de caixa"));
        Caixa fechado = caixaRepository.findById(id);
        assertTrue(fechado.getFechado());
        assertEquals(0, new BigDecimal("-20.00").compareTo(fechado.getDiferenca()));
        assertEquals("quebra de caixa", fechado.getObservacoesFechamento());
    }

    @Test
    @Transactional
    public void fecharForcado_adminSemJustificativa_falha() {
        loginComo(caixa);
        service.abrir(new AbrirCaixaDTO(new BigDecimal("100.00"), null, null));
        Caixa aberto = caixaRepository.findAbertoPorUsuario(caixa);
        loginComo(admin);
        assertThrows(BusinessException.class,
                () -> service.fecharForcado(aberto.getId(), new FecharForcadoDTO(null)));
    }

    @Test
    @Transactional
    public void fecharForcado_adminComJustificativa_fecha() {
        loginComo(caixa);
        service.abrir(new AbrirCaixaDTO(new BigDecimal("100.00"), null, null));
        Caixa aberto = caixaRepository.findAbertoPorUsuario(caixa);
        loginComo(admin);
        service.fecharForcado(aberto.getId(), new FecharForcadoDTO("Operador sumiu"));
        Caixa fechado = caixaRepository.findById(aberto.getId());
        assertTrue(fechado.getFechado());
        assertEquals(admin.getId(), fechado.getFechadoPor().getId());
    }
}
