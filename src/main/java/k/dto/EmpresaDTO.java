package k.dto;


public record EmpresaDTO(
    String nome,
    String cnpj,
    String nomeFantasia,
    Long usuarioId,
    String comentario
) {
    
}
