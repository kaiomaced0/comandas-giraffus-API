package k.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DocumentoFiscalResponseDTO(
        Long id,
        String tipo,
        String numero,
        String chaveAcesso,
        String status,
        Boolean emulado,
        Long comandaId,
        Long clienteId,
        LocalDateTime emitidoEm,
        List<Long> pagamentoIds) {
}
