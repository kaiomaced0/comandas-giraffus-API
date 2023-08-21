package k.dto;

import jakarta.validation.constraints.NotBlank;

public record EmpresaUpdateNomeDTO(
        @NotBlank String nomeFantasia) {

}
