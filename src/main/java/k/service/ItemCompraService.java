package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.model.ItemCompra;

public interface ItemCompraService {
    public List<ItemCompra> getAll();

    public Response insert(@PathParam("id") Long idPedido, ItemCompra itemCompra);

    public Response update(@PathParam("idItemCompra") Long idItemCompra, ItemCompra itemCompra);

    public Response delete(@PathParam("id") Long id);
    
}
