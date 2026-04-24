package k.dto;

import k.model.Comanda;

public record ComandaDTO(
        String nome,
        Long mesaId) {

    public ComandaDTO(String nome) {
        this(nome, null);
    }

    public static Comanda criaComanda(ComandaDTO comandaDTO) {
        Comanda p = new Comanda();
        p.setNome(comandaDTO.nome());
        p.setPreco(0.0);
        p.setFinalizada(false);
        return p;
    }
}
