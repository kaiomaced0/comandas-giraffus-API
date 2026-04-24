package k.dto;

import k.model.Cliente;

public record ClienteResponseDTO(
        Long id,
        String cpf,
        String nome,
        String email,
        Long empresaId) {

    public ClienteResponseDTO(Cliente c) {
        this(c.getId(), c.getCpf(), c.getNome(), c.getEmail(),
                c.getEmpresa() == null ? null : c.getEmpresa().getId());
    }
}
