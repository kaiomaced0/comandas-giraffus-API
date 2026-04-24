package k.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import k.model.DocumentoFiscal;
import k.model.enums.StatusEmissaoFiscal;
import k.model.enums.TipoDocumentoFiscal;

public record DocumentoFiscalResponseDTO(
        Long id,
        TipoDocumentoFiscal tipo,
        Long comandaId,
        Long clienteId,
        String numero,
        String chaveAcesso,
        StatusEmissaoFiscal statusEmissao,
        Boolean emulado,
        LocalDateTime emitidoEm,
        List<Long> pagamentosIds) {

    public DocumentoFiscalResponseDTO(DocumentoFiscal d) {
        this(d.getId(),
                d.getTipo(),
                d.getComanda() == null ? null : d.getComanda().getId(),
                d.getCliente() == null ? null : d.getCliente().getId(),
                d.getNumero(),
                d.getChaveAcesso(),
                d.getStatusEmissao(),
                d.getEmulado(),
                d.getEmitidoEm(),
                d.getPagamentos() == null
                        ? Collections.emptyList()
                        : d.getPagamentos().stream().map(p -> p.getId()).collect(Collectors.toList()));
    }
}
