package k.storage;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.minio.MinioClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * Producer CDI do cliente MinIO. As credenciais sao lidas exclusivamente de
 * variaveis de ambiente (via application.properties -> ${MINIO_*}). Nunca
 * sao logadas nem expostas em respostas.
 */
@ApplicationScoped
public class MinioProducer {

    @ConfigProperty(name = "comandas.minio.url")
    String url;

    @ConfigProperty(name = "comandas.minio.access-key")
    String accessKey;

    @ConfigProperty(name = "comandas.minio.secret-key")
    String secretKey;

    @Produces
    @ApplicationScoped
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
