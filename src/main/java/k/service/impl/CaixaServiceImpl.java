package k.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import k.model.Caixa;
import k.repository.CaixaRepository;
import k.service.CaixaService;
import k.service.UsuarioLogadoService;

public class CaixaServiceImpl implements CaixaService {

    public static final Logger LOG = Logger.getLogger(CaixaServiceImpl.class);

    @Inject
    CaixaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public List<Caixa> getAll() {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getAdmin().getId() == 
            usuarioLogadoService.getPerfilUsuarioLogado().getId()){
                LOG.info("Requisição Caixa.getAll()");
                return usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getCaixas();
            }
            else{
                
                throw new NotFoundException("Sem acesso");
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.getAll()");
            return null;
        }
        
    }

    @Override
    public Caixa getId(Long id) {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getId()== 
            repository.findById(id).getEmpresa().getId()){
                LOG.info("Requisição Caixa.getId()");
                return repository.findById(id);
            }
            else{
                
                throw new NotFoundException("Sem acesso");
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.getId()");
            return null;
        }
    }

    @Override
    public Response insert(Caixa caixa) {
        try {

            LOG.info("Requisição Caixa.insert()");
            return Response.status(Status.OK).build();
            
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.insert()");
            return Response.status(Status.NO_CONTENT).build();
        }
    }

    @Override
    public Response delete(Long id) {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getId()== 
            repository.findById(id).getEmpresa().getId()){
            Caixa entity = repository.findById(id);
            entity.setAtivo(false);
            LOG.info("Requisição Caixa.delete()");
            return Response.status(Status.OK).build();
            }
            else{
                throw new NotFoundException("Sem acesso");
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.delete()");
            return Response.status(Status.NO_CONTENT).build();
        }
    }

    @Override
    public Response fechar(Long id) {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getId()== 
            repository.findById(id).getEmpresa().getId()){
            Caixa entity = repository.findById(id);
            entity.setFechado(true);
            LOG.info("Requisição Caixa.fechar()");
            return Response.status(Status.OK).build();
            }
            else{
                throw new NotFoundException("Sem acesso");
            }
        } catch (Exception e) {
            LOG.error("Erro ao rodar Requisição Caixa.fechar()");
            return Response.status(Status.NO_CONTENT).build();
        }
    }

}
