package k.dto;

import jakarta.validation.constraints.NotBlank;

public record ItemCompraDTO(
                @NotBlank Long produtoId,
                @NotBlank Integer quantidade) {

}
