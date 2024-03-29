package k.dto;

import java.util.List;
import java.util.stream.Collectors;

import k.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        Boolean ativo,
        String nome,
        String login,
        String email,
        String cpf,
        List<PerfilResponseDTO> listaPerfil) {

    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(),usuario.getAtivo(), usuario.getNome(), usuario.getLogin(), usuario.getEmail(), usuario.getCpf(),
                usuario.getPerfis().stream().map(perfil -> new PerfilResponseDTO(perfil.getId(), perfil.getLabel()))
                        .collect(Collectors.toList()));
    }

}
