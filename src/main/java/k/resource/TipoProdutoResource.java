package k.resource;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.TipoProdutoDTO;
import k.dto.TipoProdutoResponseDTO;
import k.service.TipoProdutoService;

@Path("/tipoproduto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TipoProdutoResource {

    @Inject
    TipoProdutoService service;

    @GET
    @RolesAllowed({ "Admin", "Garcom", "Caixa", "Cozinha" })
    public List<TipoProdutoResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "Admin", "Garcom", "Caixa", "Cozinha" })
    public TipoProdutoResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @GET
    @Path("/nome/{nome}")
    @RolesAllowed({ "Admin", "Garcom", "Caixa", "Cozinha" })
    public List<TipoProdutoResponseDTO> getNome(@PathParam("nome") String nome) {
        return service.getNome(nome);
    }

    @POST
    @RolesAllowed({ "Admin", "Garcom", "Caixa" })
    public Response insert(TipoProdutoDTO tipoProdutoDTO) {

        return service.insert(tipoProdutoDTO);
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({ "Admin", "Garcom", "Caixa" })
    public Response update(@PathParam("id") Long id, TipoProdutoDTO tipoProduto) {
        return service.update(id, tipoProduto);
    }

    @PATCH
    @Path("/delete/{id}")
    @RolesAllowed({ "Admin" })
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }

}
