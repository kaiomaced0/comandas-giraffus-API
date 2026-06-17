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
import k.dto.GatewayConfigInputDTO;
import k.dto.GatewayConfigResponseDTO;
import k.dto.GatewayTesteResponseDTO;
import k.service.EmpresaGatewayConfigService;

@Path("/empresa/gateways")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "Master", "Admin" })
public class EmpresaGatewayConfigResource {

    @Inject
    EmpresaGatewayConfigService service;

    @GET
    @RolesAllowed({ "Master", "Admin" })
    public List<GatewayConfigResponseDTO> getAll() {
        return service.getAll();
    }

    @POST
    @RolesAllowed({ "Master", "Admin" })
    public Response insert(GatewayConfigInputDTO dto) {
        GatewayConfigResponseDTO created = service.insert(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PATCH
    @Path("/{id}")
    @RolesAllowed({ "Master", "Admin" })
    public GatewayConfigResponseDTO update(@PathParam("id") Long id, GatewayConfigInputDTO dto) {
        return service.update(id, dto);
    }

    @PATCH
    @Path("/delete/{id}")
    @RolesAllowed({ "Master", "Admin" })
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/testar")
    @RolesAllowed({ "Master", "Admin" })
    public GatewayTesteResponseDTO testar(@PathParam("id") Long id) {
        return service.testar(id);
    }
}
