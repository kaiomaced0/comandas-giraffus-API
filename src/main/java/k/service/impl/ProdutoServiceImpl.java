package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.ws.rs.core.Response;
import k.dto.ProdutoAdicionaRetiraDTO;
import k.dto.ProdutoDTO;
import k.dto.ProdutoResponseDTO;
import k.model.Produto;
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

    @Override
    public List<ProdutoResponseDTO> getAll() {
        return repository.listAll().stream()
                .filter(produto -> produto.getEmpresa().getId() == usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getId())
                .map(produto -> new ProdutoResponseDTO(produto))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProdutoResponseDTO> getNome(String nome) {
        return repository.findByNome(nome).stream()
                .filter(produto -> produto.getEmpresa().getId() == usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getId())
                .map(produto -> new ProdutoResponseDTO(produto))
                .collect(Collectors.toList());
    }

    @Override
    public Response getId(Long id) {
        Produto p = repository.findById(id);
        try {
            if (p.getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {
                return Response.ok(new ProdutoResponseDTO(p)).build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }

    }

    @Override
    public Response insert(ProdutoDTO produto) {
        Produto p = ProdutoDTO.criaProduto(produto);
        p.setEmpresa(usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa());
        p.setTipoProduto(tipoProdutoRepository.findById(produto.idTipoProduto()));
        repository.persist(p);
        return Response.ok(new ProdutoResponseDTO(p)).build();

    }

    @Override
    public Response update(Long idProduto, ProdutoDTO produtoDTO) {
        Produto p = repository.findById(idProduto);
        try {
            if (p.getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {
                p.setNome(produtoDTO.nome());
                p.setValorCompra(produtoDTO.valorCompra());
                p.setValorVenda(produtoDTO.valorVenda());
                p.setEstoque(produtoDTO.estoque());
                p.setTipoProduto(tipoProdutoRepository.findById(produtoDTO.idTipoProduto()));
                return Response.ok(new ProdutoResponseDTO(p)).build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }

    }

    @Override
    public Response delete(Long id) {
        Produto p = repository.findById(id);
        try {
            if (p.getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {
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
    public Response retiraEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO) {
        Produto p = repository.findById(produtoAdicionaRetiraDTO.id());
        try {
            if (p.getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {
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
    public Response adicionaEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO) {
        Produto p = repository.findById(produtoAdicionaRetiraDTO.id());
        try {
            if (p.getEmpresa() == usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa()) {
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
