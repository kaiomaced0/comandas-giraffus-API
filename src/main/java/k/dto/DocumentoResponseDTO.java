package k.dto;

import java.time.LocalDateTime;

/**
 * Representacao segura de um documento. NUNCA inclui a objectKey crua nem
 * qualquer segredo de storage.
 */
public record DocumentoResponseDTO(
        Long id,
        String tipo,
        String contentType,
        Long tamanhoBytes,
        String nomeOriginal,
        LocalDateTime dataInclusao) {
}
