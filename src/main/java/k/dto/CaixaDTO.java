package k.dto;

import k.model.Caixa;

public record CaixaDTO(
        String nome,
        String comentario) {

    public static Caixa criaCaixa(CaixaDTO caixaDTO) {
        Caixa caixa = new Caixa();
        caixa.setComentario(caixaDTO.comentario());
        caixa.setNome(caixaDTO.nome());
        return caixa;
    }

}
