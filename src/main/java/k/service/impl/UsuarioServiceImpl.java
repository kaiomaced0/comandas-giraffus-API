package k.service.impl;
import java.util.List;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import k.model.Usuario;
import k.repository.UsuarioRepository;
import k.service.UsuarioLogadoService;
import k.service.UsuarioService;

public class UsuarioServiceImpl implements UsuarioService {

    public static final Logger LOG = Logger.getLogger(UsuarioServiceImpl.class);

    @Inject
    UsuarioRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;
    
    @Override
    public List<Usuario> getAll() {
        try {
            LOG.info("Requisição Usuarios.getAll()");
            return repository.findAll().list();
            
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Usuarios.getAll()");
            return null;
        }
    }

    @Override
    public List<Usuario> getNome(String nome) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNome'");
    }

    @Override
    public Usuario findByLoginAndSenha(String login, String senha) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByLoginAndSenha'");
    }

    @Override
    public Usuario findByLogin(String login) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByLogin'");
    }

    @Override
    public List<Usuario> getId() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }

    @Override
    public Response insert(Usuario usuario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public Response update(Long idUsuario, Usuario usuario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Response delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Usuario> getFuncionarios() {
        try {
            LOG.info("Requisição Usuarios.getAll()");
            return repository.findAll().list();
            
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Usuarios.getAll()");
            return null;
        }
    }

    
}
