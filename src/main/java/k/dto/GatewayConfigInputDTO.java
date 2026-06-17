package k.dto;

public record GatewayConfigInputDTO(
        String tipo,
        String apiKey,
        String apiSecret,
        String ambiente,
        Boolean habilitado) {
}
