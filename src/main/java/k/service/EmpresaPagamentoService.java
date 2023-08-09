package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.EmpresaPagamentoDTO;
import k.dto.EmpresaPagamentoResponseDTO;

public interface EmpresaPagamentoService {
    public List<EmpresaPagamentoResponseDTO> getAll();

    public List<EmpresaPagamentoResponseDTO> getCnpj(@PathParam("cnpjEmpresa") String cnpjEmpresa);

    public EmpresaPagamentoResponseDTO getId(Long id);

    public List<EmpresaPagamentoResponseDTO> getEmpresa(Long idEmpresa);

    public Response insert(EmpresaPagamentoDTO empresaPagamento);

    public Response delete(@PathParam("id") Long id);
}
