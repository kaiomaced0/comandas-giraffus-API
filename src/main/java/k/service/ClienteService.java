package k.service;

import java.util.List;

import k.dto.ClienteInputDTO;
import k.dto.ClienteResponseDTO;

public interface ClienteService {

    ClienteResponseDTO insertOrGet(ClienteInputDTO dto);

    ClienteResponseDTO findByCpf(String cpf);

    List<ClienteResponseDTO> getAll();

    void delete(Long id);
}
