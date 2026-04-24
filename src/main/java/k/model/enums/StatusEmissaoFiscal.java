package k.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusEmissaoFiscal {
    PENDENTE(1, "Pendente"),
    EMITIDO(2, "Emitido"),
    CANCELADO(3, "Cancelado"),
    ERRO(4, "Erro");

    private final int id;
    private final String label;

    StatusEmissaoFiscal(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static StatusEmissaoFiscal valueOf(Integer id) {
        if (id == null) {
            return null;
        }
        for (StatusEmissaoFiscal s : values()) {
            if (id.equals(s.id)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Id invalido: " + id);
    }
}
