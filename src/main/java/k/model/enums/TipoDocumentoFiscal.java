package k.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoDocumentoFiscal {
    NFCE(1, "NFC-e"),
    NFE(2, "NF-e");

    private final int id;
    private final String label;

    TipoDocumentoFiscal(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static TipoDocumentoFiscal valueOf(Integer id) {
        if (id == null) {
            return null;
        }
        for (TipoDocumentoFiscal t : values()) {
            if (id.equals(t.id)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Id invalido: " + id);
    }
}
