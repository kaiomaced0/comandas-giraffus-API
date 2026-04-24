package k.service;

import java.util.List;

import jakarta.ws.rs.core.Response;
import k.dto.ClienteDTO;
import k.dto.ClienteResponseDTO;

public interface ClienteService {

    List<ClienteResponseDTO> getAll();

    ClienteResponseDTO getByCpf(String cpf);

    Response insertOrFind(ClienteDTO dto);
}
