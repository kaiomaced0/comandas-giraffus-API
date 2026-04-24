package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaDTO;
import k.dto.ComandaResponseDTO;

public interface ComandaService {

    List<ComandaResponseDTO> getAll();

    List<ComandaResponseDTO> getEmAberto();

    List<ComandaResponseDTO> getAllComandasAdm(Long idEmpresa);

    List<ComandaResponseDTO> getNome(@PathParam("nome") String nome);

    ComandaResponseDTO getId(@PathParam("id") Long id);

    Response insert(ComandaDTO comanda);

    Response delete(@PathParam("id") Long id);

    Response updatePreco(Long id);
}
