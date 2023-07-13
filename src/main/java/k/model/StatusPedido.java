package k.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusPedido {

    AGUARDANDO(1, "Aguardando"),
    PREPARANDO(2, "Preparando"),
    PRONTO(3, "Pronto"),
    ENTREGUE(4, "Entregue");

    private int id;
    private String label;

    StatusPedido(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static StatusPedido valueOf(Integer id) throws IllegalArgumentException {
        if (id == null)
            return null;
        for(StatusPedido statusPedido : StatusPedido.values()) {
            if (id.equals(statusPedido.getId()))
                return statusPedido;
        }
        throw new IllegalArgumentException("Id inválido:" + id);
    }



}
