package k.resource;

import java.util.List;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.AbrirCaixaDTO;
import k.dto.CaixaResponseDTO;
import k.dto.FecharCaixaDTO;
import k.dto.FecharForcadoDTO;
import k.dto.MovimentoCaixaDTO;
import k.service.CaixaService;
import k.service.MovimentoCaixaService;

@Path("/caixa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CaixaResource {

    @Inject
    CaixaService service;

    @Inject
    MovimentoCaixaService movimentoCaixaService;

    @GET
    @RolesAllowed({"Admin", "Caixa"})
    public List<CaixaResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @RolesAllowed({"Admin", "Caixa"})
    @Path("/{id}")
    public CaixaResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @GET
    @RolesAllowed({"Admin", "Caixa"})
    @Path("/meu")
    public CaixaResponseDTO getMeu() {
        return service.getCaixaMeu();
    }

    @GET
    @RolesAllowed({"Admin"})
    @Path("/empresa")
    public List<CaixaResponseDTO> getAbertosDaEmpresa() {
        return service.getAbertosDaEmpresa();
    }

    @POST
    @RolesAllowed({"Admin", "Caixa"})
    @Path("/abrir")
    public Response abrir(AbrirCaixaDTO dto) {
        return service.abrir(dto);
    }

    @PATCH
    @RolesAllowed({"Admin", "Caixa"})
    @Path("/fechar")
    public Response fechar(FecharCaixaDTO dto) {
        return service.fechar(dto);
    }

    @POST
    @RolesAllowed({"Admin"})
    @Path("/{id}/fechar-forcado")
    public Response fecharForcado(@PathParam("id") Long id, FecharForcadoDTO dto) {
        return service.fecharForcado(id, dto);
    }

    @PATCH
    @RolesAllowed({"Admin", "Caixa"})
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }

    @POST
    @RolesAllowed({"Admin", "Caixa"})
    @Path("/{id}/sangria")
    public Response sangria(@PathParam("id") Long id, MovimentoCaixaDTO dto) {
        return movimentoCaixaService.sangria(id, dto);
    }

    @POST
    @RolesAllowed({"Admin", "Caixa"})
    @Path("/{id}/suprimento")
    public Response suprimento(@PathParam("id") Long id, MovimentoCaixaDTO dto) {
        return movimentoCaixaService.suprimento(id, dto);
    }

    @POST
    @RolesAllowed({"Admin", "Caixa"})
    @Path("/{id}/transferir/{destinoId}")
    public Response transferir(@PathParam("id") Long id,
            @PathParam("destinoId") Long destinoId,
            MovimentoCaixaDTO dto) {
        return movimentoCaixaService.transferir(id, destinoId, dto);
    }
}
