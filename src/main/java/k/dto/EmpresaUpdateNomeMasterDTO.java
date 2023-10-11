package k.dto;

import jakarta.validation.constraints.NotBlank;

public record EmpresaUpdateNomeMasterDTO(
                @NotBlank Long idEmpresa,
                @NotBlank String nomeFantasia) {

}
