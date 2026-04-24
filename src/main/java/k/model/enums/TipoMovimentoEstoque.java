package k.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TipoMovimentoEstoque {
    VENDA(1, "Venda"),
    ENTRADA(2, "Entrada"),
    AJUSTE_NEGATIVO(3, "Ajuste negativo"),
    AJUSTE_POSITIVO(4, "Ajuste positivo");

    private final int id;
    private final String label;

    TipoMovimentoEstoque(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static TipoMovimentoEstoque valueOf(Integer id) {
        if (id == null) {
            return null;
        }
        for (TipoMovimentoEstoque t : values()) {
            if (id.equals(t.id)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Id invalido: " + id);
    }
}
