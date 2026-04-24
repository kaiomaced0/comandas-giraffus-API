package k.service;

import java.math.BigDecimal;
import java.util.List;

import jakarta.ws.rs.core.Response;
import k.dto.AbrirCaixaDTO;
import k.dto.CaixaResponseDTO;
import k.dto.FecharCaixaDTO;
import k.dto.FecharForcadoDTO;
import k.model.Caixa;

public interface CaixaService {

    List<CaixaResponseDTO> getAll();

    CaixaResponseDTO getCaixaMeu();

    List<CaixaResponseDTO> getAbertosDaEmpresa();

    List<CaixaResponseDTO> getAllFechadas();

    CaixaResponseDTO getId(Long id);

    Response abrir(AbrirCaixaDTO dto);

    Response fechar(FecharCaixaDTO dto);

    Response fecharForcado(Long idCaixa, FecharForcadoDTO dto);

    Response delete(Long id);

    BigDecimal calcularValorFechamentoEsperado(Caixa caixa);
}
