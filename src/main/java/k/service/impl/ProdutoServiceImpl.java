package k.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.MovimentoEstoqueResponseDTO;
import k.dto.ProdutoAdicionaRetiraDTO;
import k.dto.ProdutoDTO;
import k.dto.ProdutoResponseDTO;
import k.exception.BusinessException;
import k.model.MovimentoEstoque;
import k.model.Produto;
import k.model.TipoProduto;
import k.model.Usuario;
import k.model.enums.TipoMovimentoEstoque;
import k.repository.EmpresaRepository;
import k.repository.MovimentoEstoqueRepository;
import k.repository.ProdutoRepository;
import k.repository.TipoProdutoRepository;
import k.service.ProdutoService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class ProdutoServiceImpl implements ProdutoService {

    @Inject
    ProdutoRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    TipoProdutoRepository tipoProdutoRepository;

    @Inject
    EmpresaRepository empresaRepository;

    @Inject
    MovimentoEstoqueRepository movimentoEstoqueRepository;

    @Override
    public List<ProdutoResponseDTO> getAll() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        return u.getEmpresa().getProdutos().stream()
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoResponseDTO> getNome(String nome) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        return repository.findByNome(nome).stream()
                .filter(produto -> u.getEmpresa().getProdutos().contains(produto))
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Response getId(Long id) {
        Produto p = repository.findById(id);
        try {
            if (p != null && usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().contains(p)) {
                return Response.ok(new ProdutoResponseDTO(p)).build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response insert(ProdutoDTO produto) {
        Produto p = ProdutoDTO.criaProduto(produto);
        p.setTipoProduto(tipoProdutoRepository.findById(produto.idTipoProduto()));
        repository.persist(p);
        usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().add(p);
        return Response.ok(new ProdutoResponseDTO(p)).build();
    }

    @Override
    @Transactional
    public Response update(Long idProduto, ProdutoDTO produtoDTO) {
        Produto p = repository.findById(idProduto);
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().contains(p)) {
                if (produtoDTO.nome() != null) {
                    p.setNome(produtoDTO.nome());
                }
                if (produtoDTO.descricao() != null) {
                    p.setDescricao(produtoDTO.descricao());
                }
                if (produtoDTO.custo() != null) {
                    p.setValorCompra(produtoDTO.custo());
                }
                if (produtoDTO.valor() != null) {
                    p.setValorVenda(produtoDTO.valor());
                }
                if (produtoDTO.estoque() != null) {
                    p.setEstoque(produtoDTO.estoque());
                }
                TipoProduto t = tipoProdutoRepository.findById(produtoDTO.idTipoProduto());
                if (t != null) {
                    p.setTipoProduto(t);
                }
                return Response.ok(new ProdutoResponseDTO(p)).build();
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
        Produto p = repository.findById(id);
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().contains(p)) {
                p.setAtivo(false);
                return Response.ok(new ProdutoResponseDTO(p)).build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response retiraEstoque(ProdutoAdicionaRetiraDTO dto) {
        Produto p = repository.findById(dto.id());
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        if (p == null || !u.getEmpresa().getProdutos().contains(p)) {
            throw new BusinessException("Produto não pertence à empresa");
        }
        if (dto.quantidade() == null || dto.quantidade() <= 0) {
            throw new BusinessException("quantidade deve ser > 0");
        }
        Integer anterior = p.getEstoque();
        p.setEstoque((p.getEstoque() == null ? 0 : p.getEstoque()) - dto.quantidade());
        registrarMovimento(p, TipoMovimentoEstoque.AJUSTE_NEGATIVO, dto.quantidade(), dto.motivo(), u);
        return Response.ok("Valor Anterior - " + anterior + ", valor atual - " + p.getEstoque() + " .").build();
    }

    @Override
    @Transactional
    public Response adicionaEstoque(ProdutoAdicionaRetiraDTO dto) {
        Produto p = repository.findById(dto.id());
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        if (p == null || !u.getEmpresa().getProdutos().contains(p)) {
            throw new BusinessException("Produto não pertence à empresa");
        }
        if (dto.quantidade() == null || dto.quantidade() <= 0) {
            throw new BusinessException("quantidade deve ser > 0");
        }
        Integer anterior = p.getEstoque();
        p.setEstoque((p.getEstoque() == null ? 0 : p.getEstoque()) + dto.quantidade());
        registrarMovimento(p, TipoMovimentoEstoque.ENTRADA, dto.quantidade(), dto.motivo(), u);
        return Response.ok("Valor Anterior - " + anterior + ", valor atual - " + p.getEstoque() + " .").build();
    }

    @Override
    public List<MovimentoEstoqueResponseDTO> listarMovimentacoes(Long idProduto) {
        Produto p = repository.findById(idProduto);
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        if (p == null || !u.getEmpresa().getProdutos().contains(p)) {
            throw new BusinessException("Produto não pertence à empresa");
        }
        return movimentoEstoqueRepository.findByProduto(p).stream()
                .map(MovimentoEstoqueResponseDTO::new)
                .collect(Collectors.toList());
    }

    void registrarMovimento(Produto p, TipoMovimentoEstoque tipo, Integer quantidade, String motivo, Usuario u) {
        MovimentoEstoque m = new MovimentoEstoque();
        m.setProduto(p);
        m.setTipo(tipo);
        m.setQuantidade(quantidade);
        m.setMotivo(motivo);
        m.setUsuario(u);
        m.setData(LocalDateTime.now());
        movimentoEstoqueRepository.persist(m);
    }
}
