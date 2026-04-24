package k.service;

import jakarta.ws.rs.core.Response;
import k.dto.EmitirFiscalConsolidadoDTO;
import k.dto.EmitirFiscalDTO;

public interface FiscalService {

    Response emitirDaComanda(Long idComanda, EmitirFiscalDTO dto);

    Response emitirDoPagamento(Long idPagamento, EmitirFiscalDTO dto);

    Response emitirConsolidado(EmitirFiscalConsolidadoDTO dto);

    Response getId(Long id);

    Response cancelar(Long id);
}
