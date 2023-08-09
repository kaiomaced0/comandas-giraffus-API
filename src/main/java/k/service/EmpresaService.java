package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.EmpresaDTO;
import k.dto.EmpresaResponseDTO;
import k.dto.EmpresaUpdateNomeDTO;

public interface EmpresaService {
    public List<EmpresaResponseDTO> getAll();

    public List<EmpresaResponseDTO> getNome(String nome);

    public EmpresaResponseDTO getCnpj(String cnpj);

    public EmpresaResponseDTO getId(Long id);

    public Response insert(EmpresaDTO empresa);

    public Response updateNome(EmpresaUpdateNomeDTO empresaUpdateNomeDTO);

    public Response adicionarFuncionario(Long id);

    public Response removerFuncionario(Long id);

    public Response inativar(@PathParam("id") Long id);

    public Response ativar(@PathParam("id") Long id);
}
