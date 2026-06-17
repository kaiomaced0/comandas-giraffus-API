package k.dto;

/**
 * URL pre-assinada (presigned GET) de TTL curto para download temporario de
 * um documento. A URL e descartavel; expira em {@code expiraEmSegundos}.
 */
public record DocumentoUrlDTO(
        String url,
        int expiraEmSegundos) {
}
