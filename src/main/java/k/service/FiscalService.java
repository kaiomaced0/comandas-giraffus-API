package k.service;

import java.util.List;

import k.dto.DocumentoFiscalResponseDTO;
import k.dto.FiscalConsolidadoInputDTO;
import k.dto.FiscalEmissaoComandaInputDTO;
import k.dto.FiscalEmissaoPagamentoInputDTO;

public interface FiscalService {

    DocumentoFiscalResponseDTO emitirSobreComanda(Long comandaId, FiscalEmissaoComandaInputDTO dto);

    DocumentoFiscalResponseDTO emitirSobrePagamento(Long pagamentoId, FiscalEmissaoPagamentoInputDTO dto);

    DocumentoFiscalResponseDTO emitirConsolidado(FiscalConsolidadoInputDTO dto);

    DocumentoFiscalResponseDTO getById(Long id);

    DocumentoFiscalResponseDTO cancelar(Long id);

    List<DocumentoFiscalResponseDTO> getAll();
}
