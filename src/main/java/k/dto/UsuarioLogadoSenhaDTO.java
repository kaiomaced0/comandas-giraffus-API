package k.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioLogadoSenhaDTO(
        @NotBlank String senha) {

}
