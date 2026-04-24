package k.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoMovimentoCaixa {
    SANGRIA(1, "Sangria"),
    SUPRIMENTO(2, "Suprimento"),
    TRANSFERENCIA(3, "Transferencia");

    private final int id;
    private final String label;

    TipoMovimentoCaixa(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static TipoMovimentoCaixa valueOf(Integer id) {
        if (id == null) {
            return null;
        }
        for (TipoMovimentoCaixa t : values()) {
            if (id.equals(t.id)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Id invalido: " + id);
    }
}
