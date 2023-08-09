package k.dto;

import k.model.Empresa;

public record EmpresaResponseDTO(
        String nome,
        String cnpj,
        String nomeFantasia,
        Long idAdmin) {
    public EmpresaResponseDTO(Empresa empresa) {
        this(empresa.getNome(), empresa.getCnpj(), empresa.getNomeFantasia(), empresa.getAdmin().getId());
    }

}
