package k.service.impl;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import k.dto.ItemCompraDTO;
import k.dto.ItemCompraUpdateDTO;
import k.model.ItemCompra;
import k.repository.ItemCompraRepository;
import k.repository.ProdutoRepository;
import k.service.ItemCompraService;

public class ItemCompraServiceImpl implements ItemCompraService {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    ItemCompraRepository repository;

    @Override
    public List<ItemCompra> getAll() {
        throw new UnsupportedOperationException("Unimplemented method 'getAll'");
    }

    @Override
    public Response insert(ItemCompraDTO itemCompraDTO) {
        ItemCompra itemCompra = new ItemCompra();
        itemCompra.setProduto(produtoRepository.findById(itemCompraDTO.produtoId()));
        itemCompra.setQuantidade(itemCompraDTO.quantidade());
        itemCompra.setPreco(itemCompra.getProduto().getValorVenda() * itemCompra.getQuantidade());
        repository.persist(itemCompra);
        return Response.ok().build();
    }

    @Override
    public Response update(ItemCompraUpdateDTO itemCompraUpdateDTO) {
        ItemCompra entity = repository.findById(itemCompraUpdateDTO.itemCompraId());
        entity.setProduto(produtoRepository.findById(itemCompraUpdateDTO.produtoId()));
        entity.setQuantidade(itemCompraUpdateDTO.quantidade());
        entity.setPreco(entity.getQuantidade() * entity.getProduto().getValorVenda());
        return Response.ok().build();
    }

    @Override
    public Response delete(Long id) {
        repository.deleteById(id);
        return Response.ok().build();
    }

}
