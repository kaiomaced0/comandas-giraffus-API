package k.service;

import java.util.List;

import k.dto.GatewayConfigInputDTO;
import k.dto.GatewayConfigResponseDTO;
import k.dto.GatewayTesteResponseDTO;

public interface EmpresaGatewayConfigService {

    List<GatewayConfigResponseDTO> getAll();

    GatewayConfigResponseDTO insert(GatewayConfigInputDTO dto);

    GatewayConfigResponseDTO update(Long id, GatewayConfigInputDTO dto);

    void delete(Long id);

    GatewayTesteResponseDTO testar(Long id);
}
