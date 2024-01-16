package k.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.EmpresaUpdateNomeDTO;
import k.service.EmpresaService;

@Path("/gerente")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GerenteResource {

    @Inject
    EmpresaService service;

    @PATCH
    @Path("/funcionario/add/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response adicionarFuncionario(@PathParam("id") Long id) {
        return service.adicionarFuncionario(id);
    }

    @PATCH
    @Path("/mudarnomefantasia/{nome}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response updateNomeFantasia(EmpresaUpdateNomeDTO empresaUpdateNomeDTO) {
        return service.updateNomeFantasia(empresaUpdateNomeDTO);
    }

    @PATCH
    @Path("/funcionario/remove/{id}")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response removerFuncionario(@PathParam("id") Long id) {
        return service.removerFuncionario(id);
    }

}
