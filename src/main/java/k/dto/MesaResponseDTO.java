package k.dto;

import k.model.Mesa;

public record MesaResponseDTO(
        Long id,
        String identificador,
        Integer capacidade,
        Long empresaId) {

    public MesaResponseDTO(Mesa mesa) {
        this(mesa.getId(), mesa.getIdentificador(), mesa.getCapacidade(),
                mesa.getEmpresa() == null ? null : mesa.getEmpresa().getId());
    }
}
