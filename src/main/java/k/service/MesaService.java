package k.service;

import java.util.List;

import k.dto.MesaComandaDTO;
import k.dto.MesaInputDTO;
import k.dto.MesaResponseDTO;

public interface MesaService {

    List<MesaResponseDTO> getAll();

    MesaResponseDTO getById(Long id);

    MesaResponseDTO insert(MesaInputDTO dto);

    MesaResponseDTO update(Long id, MesaInputDTO dto);

    void delete(Long id);

    List<MesaComandaDTO> getComandasAbertas(Long mesaId);
}
