package k.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.PagedResponse;
import k.dto.ProdutoAdicionaRetiraDTO;
import k.dto.ProdutoDTO;
import k.dto.ProdutoResponseDTO;
import k.model.Empresa;
import k.model.EntityClass;
import k.model.Produto;
import k.model.TipoMovimentoEstoque;
import k.model.TipoProduto;
import k.model.Usuario;
import k.repository.EmpresaRepository;
import k.repository.ProdutoRepository;
import k.repository.TipoProdutoRepository;
import k.service.MovimentoEstoqueService;
import k.service.ProdutoService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;

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
    MovimentoEstoqueService movimentoEstoqueService;

    @Override
    public List<ProdutoResponseDTO> getAll() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        if (u == null || u.getEmpresa() == null) {
            return List.of();
        }
        return u.getEmpresa().getProdutos().stream().filter(EntityClass::getAtivo)
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoResponseDTO> getNome(String nome) {
        Empresa emp = usuarioLogadoService.getEmpresaLogada();
        return repository.findByNome(nome).stream()
                .filter(produto -> emp.getProdutos().contains(produto))
                .filter(EntityClass::getAtivo)
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public Response getId(Long id) {
        Produto p = repository.findById(id);
        try {
            Empresa emp = usuarioLogadoService.getEmpresaLogada();
            if (emp.getProdutos().contains(p) && p.getAtivo()) {
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
        Empresa emp = usuarioLogadoService.getEmpresaLogada();
        Produto p = ProdutoDTO.criaProduto(produto);

        p.setTipoProduto(tipoProdutoRepository.findById(produto.idTipoProduto()));
        repository.persist(p);
        emp.getProdutos().add(p);
        return Response.ok(new ProdutoResponseDTO(p)).build();

    }

    @Override
    @Transactional
    public Response update(Long idProduto, ProdutoDTO produtoDTO) {
        Produto p = repository.findById(idProduto);
        try {
            Empresa emp = usuarioLogadoService.getEmpresaLogada();
            if (emp.getProdutos().contains(p)) {
                if(produtoDTO.nome() != null){
                    p.setNome(produtoDTO.nome());
                }
                if(produtoDTO.descricao() != null){
                    p.setDescricao(produtoDTO.descricao());
                }
                if(produtoDTO.custo() != null){
                    p.setValorCompra(produtoDTO.custo());
                }
                if(produtoDTO.valor() != null){
                    p.setValorVenda(produtoDTO.valor());
                }
                if(produtoDTO.estoque() != null){
                    p.setEstoque(produtoDTO.estoque());
                }
                if(produtoDTO.imagemDocumentoId() != null){
                    p.setImagemDocumentoId(produtoDTO.imagemDocumentoId());
                }
                TipoProduto t = new TipoProduto();
                t = tipoProdutoRepository.findById(produtoDTO.idTipoProduto());
                if(t != null){
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
            Empresa emp = usuarioLogadoService.getEmpresaLogada();
            if (emp.getProdutos().contains(p)) {
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
    public Response retiraEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO) {
        Produto p = repository.findById(produtoAdicionaRetiraDTO.id());
        try {
            Empresa emp = usuarioLogadoService.getEmpresaLogada();
            if (emp.getProdutos().contains(p)) {
                Integer a = p.getEstoque();
                p.setEstoque(p.getEstoque() - produtoAdicionaRetiraDTO.quantidade());
                movimentoEstoqueService.registrar(p, TipoMovimentoEstoque.SAIDA,
                        produtoAdicionaRetiraDTO.quantidade(), null);
                return Response.ok("Valor Anterior -" + a + ", valor atual - " + p.getEstoque() + " .").build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response adicionaEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO) {
        Produto p = repository.findById(produtoAdicionaRetiraDTO.id());
        try {
            Empresa emp = usuarioLogadoService.getEmpresaLogada();
            if (emp.getProdutos().contains(p)) {
                Integer a = p.getEstoque();
                p.setEstoque(p.getEstoque() + produtoAdicionaRetiraDTO.quantidade());
                movimentoEstoqueService.registrar(p, TipoMovimentoEstoque.ENTRADA,
                        produtoAdicionaRetiraDTO.quantidade(), null);
                return Response.ok("Valor Anterior -" + a + ", valor atual - " + p.getEstoque() + " .").build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    public PagedResponse<ProdutoResponseDTO> list(
            Long tipoProdutoId,
            String search,
            Boolean emEstoque,
            int page,
            int size) {

        int p = Math.max(0, page);
        int s = Math.min(Math.max(1, size), 100);

        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            return new PagedResponse<>(List.of(), p, s, 0L);
        }
        Empresa empresa = logado.getEmpresa();

        // Multi-tenant: produto pertence à lista da empresa
        List<Long> idsProdutosEmpresa = empresa.getProdutos() == null
                ? List.of()
                : empresa.getProdutos().stream()
                        .filter(pr -> pr != null && pr.getId() != null)
                        .map(Produto::getId)
                        .collect(Collectors.toList());
        if (idsProdutosEmpresa.isEmpty()) {
            return new PagedResponse<>(List.of(), p, s, 0L);
        }

        StringBuilder ql = new StringBuilder("id in :ids and ativo = true");
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("ids", idsProdutosEmpresa);

        if (tipoProdutoId != null) {
            ql.append(" and tipoProduto.id = :tipoProdutoId");
            params.put("tipoProdutoId", tipoProdutoId);
        }
        if (search != null && !search.isBlank()) {
            ql.append(" and upper(nome) like :search");
            params.put("search", "%" + search.trim().toUpperCase() + "%");
        }
        if (Boolean.TRUE.equals(emEstoque)) {
            ql.append(" and estoque > 0");
        }

        long total = repository.count(ql.toString(), params);
        List<Produto> produtos = repository
                .find(ql.toString(), Sort.by("nome").ascending(), params)
                .page(Page.of(p, s))
                .list();

        List<ProdutoResponseDTO> data = produtos.stream()
                .map(ProdutoResponseDTO::new)
                .collect(Collectors.toList());

        return new PagedResponse<>(data, p, s, total);
    }

}
