package k.storage;

import java.io.ByteArrayInputStream;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Implementacao de {@link StorageService} sobre o SDK oficial MinIO.
 * Bucket unico privado; isolamento multi-tenant por prefixo de chave (a chave
 * e construida na camada de servico). As operacoes sao logadas sem expor
 * segredos nem a objectKey crua de outros tenants.
 */
@ApplicationScoped
public class MinioStorageService implements StorageService {

    private static final Logger LOG = Logger.getLogger(MinioStorageService.class);

    @Inject
    MinioClient minioClient;

    @ConfigProperty(name = "comandas.minio.bucket")
    String bucket;

    @Override
    public String upload(String objectKey, byte[] conteudo, String contentType) {
        try {
            ensureBucket();
            try (ByteArrayInputStream in = new ByteArrayInputStream(conteudo)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(objectKey)
                                .stream(in, conteudo.length, -1)
                                .contentType(contentType)
                                .build());
            }
            LOG.infof("Objeto enviado ao storage (%d bytes, tipo=%s)", conteudo.length, contentType);
            return objectKey;
        } catch (Exception e) {
            // NUNCA logar credenciais; mensagem generica.
            LOG.error("Falha ao enviar objeto ao storage", e);
            throw new RuntimeException("Falha ao enviar objeto ao storage: " + e.getMessage(), e);
        }
    }

    @Override
    public String presignedGet(String objectKey, int ttlSegundos) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(ttlSegundos, TimeUnit.SECONDS)
                            .build());
            LOG.infof("URL pre-assinada gerada (ttl=%ds)", ttlSegundos);
            return url;
        } catch (Exception e) {
            LOG.error("Falha ao gerar URL pre-assinada", e);
            throw new RuntimeException("Falha ao gerar URL pre-assinada: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build());
            LOG.info("Objeto removido do storage");
        } catch (Exception e) {
            LOG.error("Falha ao remover objeto do storage", e);
            throw new RuntimeException("Falha ao remover objeto do storage: " + e.getMessage(), e);
        }
    }

    private void ensureBucket() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build());
            LOG.infof("Bucket '%s' criado", bucket);
        }
    }
}
