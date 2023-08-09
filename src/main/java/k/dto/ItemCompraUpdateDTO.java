package k.dto;

import jakarta.validation.constraints.NotBlank;

public record ItemCompraUpdateDTO(
        @NotBlank Long itemCompraId,
        @NotBlank Long produtoId,
        @NotBlank Integer quantidade) {

}
