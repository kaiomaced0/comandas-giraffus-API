package k.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Metadados de um documento armazenado no storage de objetos. O conteudo
 * binario fica no MinIO; aqui guardamos apenas a referencia ({@code objectKey})
 * e os atributos de auditoria/seguranca. Multi-tenant: cada documento pertence
 * a uma {@link Empresa}; todo acesso valida a posse.
 */
@Entity
public class Documento extends EntityClass {

    @ManyToOne
    @JoinColumn(name = "empresa_documento")
    private Empresa empresa;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipo;

    private String objectKey;

    private String contentType;

    private Long tamanhoBytes;

    private String nomeOriginal;

    private String checksumSha256;

    @ManyToOne
    @JoinColumn(name = "criadopor_documento")
    private Usuario criadoPor;

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public TipoDocumento getTipo() {
        return tipo;
    }

    public void setTipo(TipoDocumento tipo) {
        this.tipo = tipo;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getTamanhoBytes() {
        return tamanhoBytes;
    }

    public void setTamanhoBytes(Long tamanhoBytes) {
        this.tamanhoBytes = tamanhoBytes;
    }

    public String getNomeOriginal() {
        return nomeOriginal;
    }

    public void setNomeOriginal(String nomeOriginal) {
        this.nomeOriginal = nomeOriginal;
    }

    public String getChecksumSha256() {
        return checksumSha256;
    }

    public void setChecksumSha256(String checksumSha256) {
        this.checksumSha256 = checksumSha256;
    }

    public Usuario getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(Usuario criadoPor) {
        this.criadoPor = criadoPor;
    }
}
