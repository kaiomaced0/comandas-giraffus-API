package k.dto;

public record GatewayTesteResponseDTO(
        boolean ok,
        String mensagem,
        boolean emulado) {
}
