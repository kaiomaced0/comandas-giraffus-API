package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.Empresa;
import k.model.Usuario;

public interface EmpresaService {
    public List<Empresa> getAll();

    public List<Empresa> getNome();

    public List<Empresa> getCnpj();

    public List<Empresa> getId();

    public Response insert(Empresa empresa);

    public Response updateNome(@PathParam("idEmpresa") Long idEmpresa, String nome);
    
    public Response update(@PathParam("idEmpresa") Long idEmpresa);

    public Response inativar(@PathParam("id") Long id);

    public Response ativar(@PathParam("id") Long id);

    public Response updateAdmin(@PathParam("idEmpresa") Long idEmpresa, Usuario novoAdmin);
}
