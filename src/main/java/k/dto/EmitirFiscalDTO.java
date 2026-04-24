package k.dto;

import k.model.enums.TipoDocumentoFiscal;

public record EmitirFiscalDTO(
        Long clienteId,
        TipoDocumentoFiscal tipo) {
}
