package k.service.impl;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.ws.rs.core.Response;
import k.dto.ItemCompraDTO;
import k.dto.ItemCompraUpdateDTO;
import k.model.ItemCompra;
import k.model.Produto;
import k.repository.ItemCompraRepository;
import k.repository.ProdutoRepository;
import k.service.ComandaService;
import k.service.ItemCompraService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ItemCompraServiceImpl implements ItemCompraService {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    ItemCompraRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    ComandaService comandaService;

    @Override
    public Response insert(ItemCompraDTO itemCompraDTO) {
        try {
            Produto p = produtoRepository.findById(itemCompraDTO.produtoId());
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa() == p.getEmpresa()) {

                ItemCompra itemCompra = new ItemCompra();
                itemCompra.setProduto(p);
                itemCompra.setQuantidade(itemCompraDTO.quantidade());
                itemCompra.setPreco(itemCompra.getProduto().getValorVenda() * itemCompra.getQuantidade());
                repository.persist(itemCompra);
                return Response.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }

    }

    @Override
    public Response update(ItemCompraUpdateDTO itemCompraUpdateDTO) {

        try {
            Produto p = produtoRepository.findById(itemCompraUpdateDTO.produtoId());
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa() == p.getEmpresa()) {

                ItemCompra entity = repository.findById(itemCompraUpdateDTO.itemCompraId());
                entity.setProduto(p);
                entity.setQuantidade(itemCompraUpdateDTO.quantidade());
                entity.setPreco(entity.getQuantidade() * entity.getProduto().getValorVenda());
                comandaService.updatePreco(entity.getPedido().getComanda().getId());
                return Response.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    public Response delete(Long id) {
        try {

            ItemCompra itemCompra = repository.findById(id);
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa() == itemCompra.getPedido().getComanda()
                    .getEmpresa()) {
                repository.delete(itemCompra);
                comandaService.updatePreco(itemCompra.getPedido().getComanda().getId());
                return Response.ok().build();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

}
