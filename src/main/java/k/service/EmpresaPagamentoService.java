package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.EmpresaPagamento;

public interface EmpresaPagamentoService {
    public List<EmpresaPagamento> getAll();

    public List<EmpresaPagamento> getCnpj(@PathParam("cnpjEmpresa") String cnpjEmpresa);

    public List<EmpresaPagamento> getId();

    public Response insert(EmpresaPagamento empresaPagamento);

    public Response delete(@PathParam("id") Long id);
}
