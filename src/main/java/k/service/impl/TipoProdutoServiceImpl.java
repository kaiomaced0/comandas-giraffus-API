package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.TipoProdutoDTO;
import k.dto.TipoProdutoResponseDTO;
import k.model.TipoProduto;
import k.repository.EmpresaRepository;
import k.repository.TipoProdutoRepository;
import k.service.TipoProdutoService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class TipoProdutoServiceImpl implements TipoProdutoService {

    @Inject
    TipoProdutoRepository repository;

    @Inject
    EmpresaRepository empresaRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;
    public static final Logger LOG = Logger.getLogger(TipoProdutoServiceImpl.class);

    @Override
    public List<TipoProdutoResponseDTO> getAll() {
        return empresaRepository.findById(usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getId())
                .getTipoProdutos().stream()
                .filter(tipoProduto -> usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getTipoProdutos().contains(tipoProduto))
                .map(tipoProduto -> new TipoProdutoResponseDTO(tipoProduto.getId(), tipoProduto.getNome(), tipoProduto.getCor()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TipoProdutoResponseDTO> getNome(String nome) {
        return repository.findByNome(nome).stream().filter(
                tipoProduto -> usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getTipoProdutos().contains(tipoProduto))
                .map(tipoProduto -> new TipoProdutoResponseDTO(tipoProduto.getId(), tipoProduto.getNome(), tipoProduto.getCor()))
                .collect(Collectors.toList());
    }

    @Override
    public TipoProdutoResponseDTO getId(Long id) {
        TipoProduto entity = repository.findById(id);
        return new TipoProdutoResponseDTO(entity.getId(), entity.getNome(), entity.getCor());
    }

    @Override
    @Transactional
    public Response insert(TipoProdutoDTO tipoProduto) {
        TipoProduto entity = TipoProdutoDTO.criaTipoProduto(tipoProduto);
        usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getTipoProdutos().add(entity);
        repository.persist(entity);
        return Response.ok(new TipoProdutoResponseDTO(entity.getId(), entity.getNome(), entity.getCor())).build();
    }

    @Override
    @Transactional
    public Response update(Long idTipoProduto, TipoProdutoDTO tipoProduto) {
        try {
            LOG.info("Entrou no update tipoproduto nome:" +tipoProduto.nome() + "  cor:" + tipoProduto.cor() + " .");
            TipoProduto entity = repository.findById(idTipoProduto);
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getTipoProdutos().contains(entity)) {
                entity.setNome(tipoProduto.nome());
                entity.setCor(tipoProduto.cor());
                return Response.ok(new TipoProdutoResponseDTO(entity.getId(), entity.getNome(), entity.getCor())).build();

            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        TipoProduto entity = repository.findById(id);
        entity.setAtivo(false);
        return Response.ok(new TipoProdutoResponseDTO(entity.getId(), entity.getNome(), entity.getCor())).build();
    }

}
