package k.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import k.dto.MovimentoEstoqueResponseDTO;
import k.model.Empresa;
import k.model.MovimentoEstoque;
import k.model.Produto;
import k.model.TipoMovimentoEstoque;
import k.model.Usuario;
import k.repository.MovimentoEstoqueRepository;
import k.repository.ProdutoRepository;
import k.service.MovimentoEstoqueService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class MovimentoEstoqueServiceImpl implements MovimentoEstoqueService {

    public static final Logger LOG = Logger.getLogger(MovimentoEstoqueServiceImpl.class);

    @Inject
    MovimentoEstoqueRepository repository;

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    @Transactional
    public MovimentoEstoqueResponseDTO registrar(Produto p, TipoMovimentoEstoque tipo, int quantidade, String motivo) {
        if (p == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        if (tipo == null) {
            throw new WebApplicationException("Tipo é obrigatório", Response.Status.BAD_REQUEST);
        }
        if (quantidade <= 0) {
            throw new WebApplicationException("Quantidade deve ser positiva", Response.Status.BAD_REQUEST);
        }
        MovimentoEstoque m = new MovimentoEstoque();
        m.setProduto(p);
        m.setTipo(tipo);
        m.setQuantidade(quantidade);
        m.setMotivo(motivo);
        Usuario logado = null;
        try {
            logado = usuarioLogadoService.getPerfilUsuarioLogado();
        } catch (Exception ignored) {
            // sem contexto de usuário
        }
        m.setUsuario(logado);
        repository.persist(m);
        LOG.info("MovimentoEstoque criado id=" + m.getId() + " tipo=" + tipo + " produtoId=" + p.getId());
        return toResponse(m);
    }

    @Override
    public List<MovimentoEstoqueResponseDTO> getByProduto(Long produtoId) {
        Produto p = findOwned(produtoId);
        return repository.findByProduto(p).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private Produto findOwned(Long id) {
        if (id == null) {
            throw new NotFoundException("Produto não encontrado");
        }
        Produto produto = produtoRepository.findById(id);
        if (produto == null || !Boolean.TRUE.equals(produto.getAtivo())) {
            throw new NotFoundException("Produto não encontrado");
        }
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        Empresa empresaLogada = logado == null ? null : logado.getEmpresa();
        if (empresaLogada == null || empresaLogada.getProdutos() == null
                || empresaLogada.getProdutos().stream().noneMatch(pr -> pr != null && produto.getId().equals(pr.getId()))) {
            throw new NotFoundException("Produto não encontrado");
        }
        return produto;
    }

    private MovimentoEstoqueResponseDTO toResponse(MovimentoEstoque m) {
        LocalDate data = m.getDataInclusao() == null ? null : m.getDataInclusao().toLocalDate();
        Long produtoId = m.getProduto() == null ? null : m.getProduto().getId();
        String tipo = m.getTipo() == null ? null : m.getTipo().name();
        return new MovimentoEstoqueResponseDTO(m.getId(), produtoId, tipo, m.getQuantidade(), m.getMotivo(), data);
    }
}
