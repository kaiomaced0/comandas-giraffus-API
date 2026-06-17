package k.dto;

public record GatewayConfigResponseDTO(
        Long id,
        String tipo,
        String ambiente,
        Boolean habilitado,
        String apiKeyMascarada,
        boolean temSecret) {
}
