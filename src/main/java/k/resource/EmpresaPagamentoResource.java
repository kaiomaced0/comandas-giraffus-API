package k.resource;

import java.util.List;

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
import k.dto.EmpresaPagamentoDTO;
import k.dto.EmpresaPagamentoResponseDTO;
import k.service.EmpresaPagamentoService;

@Path("/empresapagamento")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EmpresaPagamentoResource {

    @Inject
    EmpresaPagamentoService service;

    @GET
    public List<EmpresaPagamentoResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @Path("/{id}")
    public EmpresaPagamentoResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @GET
    @Path("/cnpj/{cnpj}")
    public List<EmpresaPagamentoResponseDTO> getCnpj(@PathParam("cnpj") String cnpj) {
        return service.getCnpj(cnpj);
    }

    @GET
    @Path("/empresa/{id}")
    public List<EmpresaPagamentoResponseDTO> getEmpresa(@PathParam("id") Long id) {
        return service.getEmpresa(id);
    }

    @POST
    public Response insert(EmpresaPagamentoDTO empresaPagamentoDTO) {
        return service.insert(empresaPagamentoDTO);

    }

    @PATCH
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }
}
