package k.dto;

import k.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String cpf,
        Long idEMpresa) {

    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getCpf(), usuario.getEmpresa().getId());
    }

}
