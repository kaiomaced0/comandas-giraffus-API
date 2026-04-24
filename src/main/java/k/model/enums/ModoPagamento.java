package k.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ModoPagamento {
    SIMPLES(1, "Simples"),
    RATEADO(2, "Rateado");

    private final int id;
    private final String label;

    ModoPagamento(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static ModoPagamento valueOf(Integer id) {
        if (id == null) {
            return null;
        }
        for (ModoPagamento modo : values()) {
            if (id.equals(modo.id)) {
                return modo;
            }
        }
        throw new IllegalArgumentException("Id invalido: " + id);
    }
}
