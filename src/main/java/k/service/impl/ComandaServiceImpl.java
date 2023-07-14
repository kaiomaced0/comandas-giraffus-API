package k.service.impl;
import java.util.List;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import k.model.Comanda;
import k.repository.ComandaRepository;
import k.service.ComandaService;
import k.service.UsuarioLogadoService;

public class ComandaServiceImpl implements ComandaService {
    
    public static final Logger LOG = Logger.getLogger(ComandaServiceImpl.class);

    @Inject
    ComandaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;
    
    @Override
    public List<Comanda> getAll() {
        try {
            LOG.info("Requisição Comandas.getAll()");
            return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas();
            
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Comandas.getAll()");
            return null;
        }
    }

    @Override
    public List<Comanda> getNome(String nome) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getNome'");
    }

    @Override
    public Comanda getId(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getId'");
    }

    @Override
    public Response insert(Comanda comanda) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insert'");
    }

    @Override
    public Response pagar(Long id, Long idPagamento) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pagar'");
    }

    @Override
    public Response delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public List<Comanda> getEmAberto() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEmAberto'");
    }

    @Override
    public List<Comanda> getMyComandas() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMyComandas'");
    }
    
}
