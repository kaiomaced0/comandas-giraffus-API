package k.service.impl;
import java.util.List;

import jakarta.ws.rs.core.Response;
import k.model.Empresa;
import k.model.Usuario;
import k.service.EmpresaService;

public class EmpresaServiceImpl implements EmpresaService {

    @Override
    public List<Empresa> getAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public List<Empresa> getNome() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNome'");
    }

    @Override
    public List<Empresa> getCnpj() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCnpj'");
    }

    @Override
    public List<Empresa> getId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }

    @Override
    public Response insert(Empresa empresa) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public Response updateNome(Long idEmpresa, String nome) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateNome'");
    }

    @Override
    public Response update(Long idEmpresa) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Response inativar(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'inativar'");
    }

    @Override
    public Response ativar(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ativar'");
    }

    @Override
    public Response updateAdmin(Long idEmpresa, Usuario novoAdmin) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateAdmin'");
    }
    
}
