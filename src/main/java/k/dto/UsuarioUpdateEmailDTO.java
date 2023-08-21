package k.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioUpdateEmailDTO(
        @NotBlank String email) {

}
