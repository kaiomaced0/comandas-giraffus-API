package k.dto;

import java.util.List;

import k.model.enums.TipoDocumentoFiscal;

public record EmitirFiscalConsolidadoDTO(
        List<Long> pagamentoIds,
        Long clienteId,
        TipoDocumentoFiscal tipo) {
}
