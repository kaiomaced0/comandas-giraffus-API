package k.resource;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.ProdutoAdicionaRetiraDTO;
import k.dto.ProdutoDTO;
import k.dto.ProdutoResponseDTO;
import k.service.ProdutoService;

@Path("/produto")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject
    ProdutoService service;

    @GET
    @RolesAllowed({ "Master", "Admin", "Garcom", "Caixa", "Cozinha" })
    public List<ProdutoResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "Admin", "Garcom", "Caixa", "Cozinha" })
    public Response getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @GET
    @Path("/nome/{nome}")
    @RolesAllowed({ "Admin", "Garcom", "Caixa", "Cozinha" })
    public List<ProdutoResponseDTO> getNome(@PathParam("nome") String nome) {
        return service.getNome(nome);
    }

    @POST
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response insert(ProdutoDTO Produto) {
        return service.insert(Produto);
    }

    @PATCH
    @Path("/update/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response update(@PathParam("id") Long id, ProdutoDTO produtoDTO) {
        return service.update(id, produtoDTO);
    }

    @PATCH
    @Path("/adicionarestoque")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response adicionarEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO) {
        return service.adicionaEstoque(produtoAdicionaRetiraDTO);
    }

    @PATCH
    @Path("/retirarestoque")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response retirarEstoque(ProdutoAdicionaRetiraDTO produtoAdicionaRetiraDTO) {
        return service.retiraEstoque(produtoAdicionaRetiraDTO);
    }

    @PATCH
    @Path("/delete/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }

}
