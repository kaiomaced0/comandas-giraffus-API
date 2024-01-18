package k.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import k.dto.*;
import k.service.EmpresaService;
import k.service.UsuarioService;

import java.util.List;

@Path("/gerente")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GerenteResource {

    @Inject
    EmpresaService service;

    @Inject
    UsuarioService usuarioService;

    @POST
    @Path("/funcionario/add")
    @RolesAllowed({ "Admin" })
    @Transactional
    public Response adicionarFuncionario(UsuarioDTO usuarioDTO) {
        return usuarioService.insertFuncionario(usuarioDTO);
    }

    @PATCH
    @RolesAllowed({ "Admin" })
    @Path("/funcionario/update/nome")
    @Transactional
    public Response updateNomeUsuario(UsuarioUpdateNomeGerenteDTO usuarioUpdateNomeGerenteDTO) {
        return usuarioService.updateNomeGerente(usuarioUpdateNomeGerenteDTO);
    }

    @PATCH
    @RolesAllowed({ "Admin" })
    @Path("/funcionario/update/senha")
    @Transactional
    public Response updateSenhaUsuario(UsuarioUpdateSenhaGerenteDTO usuarioUpdateSenhaGerenteDTO) {
        return usuarioService.updateSenhaGerente(usuarioUpdateSenhaGerenteDTO);
    }

    @RolesAllowed({ "Admin" })
    @GET
    @Path("/funcionarios")
    public List<UsuarioResponseDTO> getFuncionario() {
        return usuarioService.getFuncionarios();
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
