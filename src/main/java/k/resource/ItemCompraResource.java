package k.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.ItemCompraDTO;
import k.dto.ItemCompraUpdateDTO;
import k.service.ItemCompraService;

@Path("/itemcompra")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ItemCompraResource {

    @Inject
    ItemCompraService service;

    @POST
    @RolesAllowed({ "Master", "Admin", "Garcom", "Caixa" })
    @Transactional
    public Response insert(ItemCompraDTO itemCompraDTO) {
        return service.insert(itemCompraDTO);

    }

    @PUT
    @Path("/update")
    @RolesAllowed({ "Master", "Admin", "Garcom", "Caixa" })
    @Transactional
    public Response update(ItemCompraUpdateDTO itemCompraUpdateDTO) {
        return service.update(itemCompraUpdateDTO);
    }

    @PUT
    @Path("/delete/{id}")
    @RolesAllowed({ "Master", "Admin", "Garcom", "Caixa" })
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }

}
