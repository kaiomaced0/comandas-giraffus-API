package k.service;

import java.util.List;

import jakarta.ws.rs.core.Response;
import k.dto.ComandaResponseDTO;
import k.dto.MesaDTO;
import k.dto.MesaResponseDTO;

public interface MesaService {

    List<MesaResponseDTO> getAll();

    MesaResponseDTO getId(Long id);

    Response insert(MesaDTO dto);

    Response update(Long id, MesaDTO dto);

    Response delete(Long id);

    List<ComandaResponseDTO> getComandas(Long idMesa);
}
