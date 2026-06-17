package k.storage;

/**
 * Abstracao de armazenamento de objetos. A API e o unico broker: o cliente
 * nunca recebe credenciais; downloads ocorrem somente via URL pre-assinada de
 * TTL curto. Implementacao padrao: {@link MinioStorageService}.
 */
public interface StorageService {

    /**
     * Envia o conteudo para o storage sob a chave informada, garantindo a
     * existencia do bucket. Retorna a objectKey persistida.
     */
    String upload(String objectKey, byte[] conteudo, String contentType);

    /**
     * Gera uma URL pre-assinada (HTTP GET) de leitura temporaria para a chave,
     * valida por {@code ttlSegundos} segundos.
     */
    String presignedGet(String objectKey, int ttlSegundos);

    /**
     * Remove o objeto identificado pela chave.
     */
    void delete(String objectKey);
}
