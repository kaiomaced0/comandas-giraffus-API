package k.model;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FormaPagamento {

    CREDITO(1, "Cartao Credito"),
    DEBITO(2, "Cartao Debito"),
    PIX(3, "Pix"),
    AVISTA(4, "A vista");

    private int id;
    private String label;

    FormaPagamento(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public static FormaPagamento valueOf(Integer id) throws IllegalArgumentException {
        if (id == null)
            return null;
        for(FormaPagamento formaPagamento : FormaPagamento.values()) {
            if (id.equals(formaPagamento.getId()))
                return formaPagamento;
        }
        throw new IllegalArgumentException("Id inválido:" + id);
    }



}
