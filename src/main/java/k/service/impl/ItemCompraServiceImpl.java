package k.service.impl;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.EmpresaResponseDTO;
import k.dto.ItemCompraDTO;
import k.dto.ItemCompraResponseDTO;
import k.dto.ItemCompraUpdateDTO;
import k.model.Empresa;
import k.model.ItemCompra;
import k.model.Produto;
import k.repository.ItemCompraRepository;
import k.repository.ProdutoRepository;
import k.service.ComandaService;
import k.service.ItemCompraService;
import k.service.UsuarioLogadoService;

import java.util.List;
import java.util.stream.Collectors;

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

    // @Override
    // public List<ItemCompraResponseDTO> getAll() {
    //     return repository.findAll().stream().filter(ItemCompra::getAtivo)
    //             .filter(item -> usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getComandas()
    //                     .contains(item.getPedido().getComanda()))
    //             .map(ItemCompraResponseDTO::new)
    //             .collect(Collectors.toList());
    // }

    @Override
    @Transactional
    public ItemCompra insert(ItemCompraDTO itemCompraDTO) {
        try {
            Produto p = produtoRepository.findById(itemCompraDTO.produtoId());
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().contains(p)) {

                ItemCompra itemCompra = new ItemCompra();
                itemCompra.setProduto(p);
                itemCompra.setQuantidade(itemCompraDTO.quantidade());
                itemCompra.setPreco(itemCompra.getProduto().getValorVenda() * itemCompra.getQuantidade());
                repository.persist(itemCompra);
                return itemCompra;
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    @Transactional
    public Response update(ItemCompraUpdateDTO itemCompraUpdateDTO) {

        try {
            Produto p = produtoRepository.findById(itemCompraUpdateDTO.produtoId());
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getProdutos().contains(p)) {

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
    @Transactional
    public Response delete(Long id) {
        try {

            ItemCompra itemCompra = repository.findById(id);
            repository.delete(itemCompra);
            comandaService.updatePreco(itemCompra.getPedido().getComanda().getId());
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

}
