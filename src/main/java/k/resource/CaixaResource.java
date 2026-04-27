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
import jakarta.ws.rs.core.Response.Status;
import k.dto.CaixaAbrirInputDTO;
import k.dto.CaixaDTO;
import k.dto.CaixaFecharInputDTO;
import k.dto.CaixaForcadoFecharInputDTO;
import k.dto.CaixaResponseDTO;
import k.dto.MovimentoCaixaInputDTO;
import k.dto.MovimentoCaixaResponseDTO;
import k.dto.TransferenciaInputDTO;
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
    @RolesAllowed({ "Admin", "Caixa" })
    public List<CaixaResponseDTO> getAll() {
        return service.getAll();
    }

    @GET
    @RolesAllowed({ "Admin", "Caixa" })
    @Path("/{id}")
    public CaixaResponseDTO getId(@PathParam("id") Long id) {
        return service.getId(id);
    }

    @GET
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    @Path("/atual")
    public CaixaResponseDTO getAtual() {
        // Compat: agora redireciona semanticamente para meuCaixaAberto.
        return service.getCaixaAtual();
    }

    @POST
    @RolesAllowed({ "Admin", "Caixa" })
    @Transactional
    public Response insert(CaixaDTO caixa) {
        return service.insert(caixa);
    }

    @PATCH
    @RolesAllowed({ "Admin", "Caixa" })
    @Path("/delete/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        return service.delete(id);
    }

    /**
     * @deprecated use {@link #fecharComBody(Long, CaixaFecharInputDTO)} instead.
     */
    @Deprecated
    @PATCH
    @RolesAllowed({ "Admin", "Caixa" })
    @Path("/fechar-legacy/{id}")
    @Transactional
    public Response fecharLegacy(@PathParam("id") Long id) {
        return service.fechar(id);
    }

    @PATCH
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    @Path("/fechar/{id}")
    @Transactional
    public Response fecharComBody(@PathParam("id") Long id, CaixaFecharInputDTO dto) {
        CaixaResponseDTO resp = service.fechar(id, dto);
        return Response.ok(resp).build();
    }

    @POST
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    @Path("/abrir")
    @Transactional
    public Response abrir(CaixaAbrirInputDTO dto) {
        CaixaResponseDTO resp = service.abrir(dto);
        return Response.status(Status.CREATED).entity(resp).build();
    }

    @GET
    @RolesAllowed({ "Master", "Admin", "Caixa", "Garcom", "Cozinha" })
    @Path("/meu")
    public CaixaResponseDTO meuCaixaAberto() {
        return service.meuCaixaAberto();
    }

    @GET
    @RolesAllowed({ "Master", "Admin" })
    @Path("/empresa")
    public List<CaixaResponseDTO> abertosNaEmpresa() {
        return service.abertosNaEmpresa();
    }

    @POST
    @RolesAllowed({ "Master", "Admin" })
    @Path("/{id}/fechar-forcado")
    @Transactional
    public CaixaResponseDTO fecharForcado(@PathParam("id") Long id, CaixaForcadoFecharInputDTO dto) {
        CaixaFecharInputDTO inner = new CaixaFecharInputDTO(
                dto == null ? null : dto.valorFechamentoInformado(),
                dto == null ? null : dto.observacoesFechamento());
        String justificativa = dto == null ? null : dto.justificativa();
        return service.fecharForcado(id, inner, justificativa);
    }

    @POST
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    @Path("/{id}/sangria")
    public MovimentoCaixaResponseDTO sangria(@PathParam("id") Long id, MovimentoCaixaInputDTO dto) {
        return movimentoCaixaService.sangria(id, dto);
    }

    @POST
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    @Path("/{id}/suprimento")
    public MovimentoCaixaResponseDTO suprimento(@PathParam("id") Long id, MovimentoCaixaInputDTO dto) {
        return movimentoCaixaService.suprimento(id, dto);
    }

    @POST
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    @Path("/{id}/transferir")
    public MovimentoCaixaResponseDTO transferir(@PathParam("id") Long id, TransferenciaInputDTO dto) {
        return movimentoCaixaService.transferir(id, dto);
    }

    @GET
    @RolesAllowed({ "Master", "Admin", "Caixa" })
    @Path("/{id}/movimentos")
    public List<MovimentoCaixaResponseDTO> getMovimentos(@PathParam("id") Long id) {
        return movimentoCaixaService.getByCaixa(id);
    }
}
