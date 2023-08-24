package k.dto;

import java.util.List;
import java.util.stream.Collectors;

import k.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String cpf,
        List<PerfilResponseDTO> listaPerfil) {

    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getCpf(),
                usuario.getPerfis().stream().map(perfil -> new PerfilResponseDTO(perfil.getId(), perfil.getLabel()))
                        .collect(Collectors.toList()));
    }

}
