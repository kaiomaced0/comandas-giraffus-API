package k.dto;

import k.model.Usuario;

public record UsuarioDTO(
        String nome,
        String login,
        String senha,
        String cpf,
        Long idEmpresa,
        Integer idPerfil) {
    public static Usuario criaUsuario(UsuarioDTO usuarioDTO) {
        Usuario entity = new Usuario();
        entity.setNome(usuarioDTO.nome());
        entity.setLogin(usuarioDTO.login());
        entity.setCpf(usuarioDTO.cpf());
        return entity;
    }

}
