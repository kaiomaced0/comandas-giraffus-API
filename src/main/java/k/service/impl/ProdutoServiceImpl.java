package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.ProdutoAdicionaRetiraDTO;
import k.dto.ProdutoDTO;
import k.dto.ProdutoResponseDTO;
import k.model.Produto;
import k.model.TipoProduto;
import k.repository.EmpresaRepository;
import k.repository.ProdutoRepository;
import k.repository.TipoProdutoRepository;
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

    @Override
    public List<ProdutoResponseDTO> getAll() {
        return repository.listAll().stream()
                .filter(produto -> usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getProdutos().contains(produto))
                .map(produto -> new ProdutoResponseDTO(produto))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoResponseDTO> getNome(String nome) {
        return repository.findByNome(nome).stream()
                .filter(produto -> usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getProdutos().contains(produto))
                .map(produto -> new ProdutoResponseDTO(produto))
                .collect(Collectors.toList());
    }

    @Override
    public Response getId(Long id) {
        Produto p = repository.findById(id);
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().contains(p)) {
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
    public Response retiraEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO) {
        Produto p = repository.findById(produtoAdicionaRetiraDTO.id());
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().contains(p)) {
                Integer a = p.getEstoque();
                p.setEstoque(p.getEstoque() - produtoAdicionaRetiraDTO.quantidade());
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
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().contains(p)) {
                Integer a = p.getEstoque();
                p.setEstoque(p.getEstoque() + produtoAdicionaRetiraDTO.quantidade());
                return Response.ok("Valor Anterior -" + a + ", valor atual - " + p.getEstoque() + " .").build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

}
