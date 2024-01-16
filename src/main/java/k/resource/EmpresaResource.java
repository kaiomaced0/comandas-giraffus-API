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
import k.dto.EmpresaDTO;
import k.dto.EmpresaResponseDTO;
import k.dto.EmpresaUpdateNomeMasterDTO;
import k.service.EmpresaService;

@Path("/empresa")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmpresaResource {

    @Inject
    EmpresaService service;

    @GET
    @RolesAllowed({ "Master" })
    public List<EmpresaResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/off")
    @RolesAllowed({ "Master" })
    public List<EmpresaResponseDTO> getInativos() {
        return service.getAllInativos();
    }

    @GET
    @Path("/{id}")
    @RolesAllowed({ "Master" })
    public EmpresaResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @GET
    @Path("/nome/{nome}")
    @RolesAllowed({ "Master" })
    public List<EmpresaResponseDTO> getNome(@PathParam("nome") String nome) {
        return service.getNome(nome);
    }

    @GET
    @Path("/cnpj/{cnpj}")
    @RolesAllowed({ "Master" })
    public List<EmpresaResponseDTO> getCnpj(@PathParam("cnpj") String cnpj) {
        return service.getCnpj(cnpj);
    }

    @POST
    @RolesAllowed({ "Master" })
    @Transactional
    public Response insert(EmpresaDTO Empresa) {
        return service.insert(Empresa);
    }

    @PATCH
    @Path("/mudarnomefantasia/{nome}")
    @RolesAllowed({ "Master" })
    @Transactional
    public Response updateNomeFantasia(EmpresaUpdateNomeMasterDTO empresaUpdateNomeDTO) {
        return service.updateNomeFantasiaMaster(empresaUpdateNomeDTO);
    }

    @PATCH
    @Path("/delete/{id}")
    @RolesAllowed({ "Master", "Admin" })
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        return service.inativar(id);
    }

    @PATCH
    @Path("/ativar/{id}")
    @RolesAllowed("Master")
    @Transactional
    public Response ativar(@PathParam("id") Long id) {
        return service.ativar(id);
    }

}
