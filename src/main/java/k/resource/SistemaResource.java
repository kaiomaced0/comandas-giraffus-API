package k.resource;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import k.dto.ProdutoSistemaResponseDTO;
import k.repository.ProdutoRepository;

@Path("/sistema")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SistemaResource {

    @Inject
    ProdutoRepository produtoRepository;

    @GET
    @Path("/produtos")
    @PermitAll
    public List<ProdutoSistemaResponseDTO> getAllProdutos() {
        return produtoRepository.findAll().stream().map(ProdutoSistemaResponseDTO::new)
                .collect(Collectors.toList());
    }

}
