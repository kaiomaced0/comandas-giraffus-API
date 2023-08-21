package k.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateLoginDTO(
        @NotBlank String login) {

}
