package k.service;

import java.time.LocalDate;
import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaDTO;
import k.dto.ComandaPagarDTO;
import k.dto.ComandaPagedItemDTO;
import k.dto.ComandaResponseDTO;
import k.dto.PagedResponse;

public interface ComandaService {
    public List<ComandaResponseDTO> getAll();

    public List<ComandaResponseDTO> getEmAberto();

    public List<ComandaResponseDTO> getAllComandasAdm(Long idEmpresa);

    public List<ComandaResponseDTO> getNome(@PathParam("nome") String nome);

    public ComandaResponseDTO getId(@PathParam("id") Long id);

    public Response insert(ComandaDTO comanda);

    public Response pagar(ComandaPagarDTO comandaPagarDTO);

    public Response delete(@PathParam("id") Long id);

    public Response updatePreco(Long id);

    /**
     * Lista paginada de comandas da empresa do usuário logado.
     *
     * @param mesaId       filtro opcional por mesa
     * @param finalizada   filtro opcional por status finalizada
     * @param from         data mínima (inclusive) de inclusão (ISO LocalDate)
     * @param to           data máxima (inclusive) de inclusão (ISO LocalDate)
     * @param atendenteId  filtro opcional por atendente
     * @param page         página (0-based)
     * @param size         tamanho da página
     */
    public PagedResponse<ComandaPagedItemDTO> list(
            Long mesaId,
            Boolean finalizada,
            LocalDate from,
            LocalDate to,
            Long atendenteId,
            int page,
            int size);
}
