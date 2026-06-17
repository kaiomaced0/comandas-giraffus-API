package k.resource;

import java.util.List;
import java.util.Map;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import k.dto.DocumentoResponseDTO;
import k.dto.DocumentoUrlDTO;
import k.service.DocumentoService;

@Path("/documentos")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({ "Master", "Admin", "Caixa" })
public class DocumentoResource {

    @Inject
    DocumentoService service;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(MultipartFormDataInput input) {
        if (input == null) {
            throw new WebApplicationException("Requisicao multipart invalida", Response.Status.BAD_REQUEST);
        }
        Map<String, List<InputPart>> form = input.getFormDataMap();

        InputPart filePart = primeiraParte(form, "file");
        if (filePart == null) {
            throw new WebApplicationException("Parte 'file' obrigatoria", Response.Status.BAD_REQUEST);
        }

        byte[] bytes = lerBytes(filePart);
        String contentType = filePart.getMediaType() == null ? null : filePart.getMediaType().toString();

        String tipo = lerTexto(primeiraParte(form, "tipo"));
        String nome = lerTexto(primeiraParte(form, "nome"));
        if (nome == null || nome.isBlank()) {
            nome = nomeArquivo(filePart);
        }

        DocumentoResponseDTO created = service.upload(tipo, nome, contentType, bytes);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    public List<DocumentoResponseDTO> listar() {
        return service.listar();
    }

    @GET
    @Path("/{id}/url")
    public DocumentoUrlDTO url(@PathParam("id") Long id) {
        return service.urlTemporaria(id);
    }

    @PATCH
    @Path("/delete/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.excluir(id);
        return Response.noContent().build();
    }

    // ----- helpers de extracao do multipart -----

    private static InputPart primeiraParte(Map<String, List<InputPart>> form, String chave) {
        if (form == null) {
            return null;
        }
        List<InputPart> partes = form.get(chave);
        if (partes == null || partes.isEmpty()) {
            return null;
        }
        return partes.get(0);
    }

    private static byte[] lerBytes(InputPart part) {
        try {
            return part.getBody(byte[].class, null);
        } catch (Exception e) {
            throw new WebApplicationException("Falha ao ler o arquivo enviado", Response.Status.BAD_REQUEST);
        }
    }

    private static String lerTexto(InputPart part) {
        if (part == null) {
            return null;
        }
        try {
            return part.getBodyAsString();
        } catch (Exception e) {
            throw new WebApplicationException("Falha ao ler campo de texto do formulario",
                    Response.Status.BAD_REQUEST);
        }
    }

    /**
     * Extrai o filename do header Content-Disposition da parte (quando o
     * cliente nao envia o campo 'nome' explicitamente).
     */
    private static String nomeArquivo(InputPart part) {
        MultivaluedMap<String, String> headers = part.getHeaders();
        if (headers == null) {
            return null;
        }
        String disposition = headers.getFirst("Content-Disposition");
        if (disposition == null) {
            return null;
        }
        for (String token : disposition.split(";")) {
            String t = token.trim();
            if (t.startsWith("filename")) {
                int eq = t.indexOf('=');
                if (eq >= 0) {
                    String value = t.substring(eq + 1).trim();
                    if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
                        value = value.substring(1, value.length() - 1);
                    }
                    return value;
                }
            }
        }
        return null;
    }
}
