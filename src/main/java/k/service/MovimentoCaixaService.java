package k.service;

import java.util.List;

import k.dto.MovimentoCaixaInputDTO;
import k.dto.MovimentoCaixaResponseDTO;
import k.dto.TransferenciaInputDTO;

public interface MovimentoCaixaService {

    MovimentoCaixaResponseDTO sangria(Long caixaId, MovimentoCaixaInputDTO dto);

    MovimentoCaixaResponseDTO suprimento(Long caixaId, MovimentoCaixaInputDTO dto);

    MovimentoCaixaResponseDTO transferir(Long caixaOrigemId, TransferenciaInputDTO dto);

    List<MovimentoCaixaResponseDTO> getByCaixa(Long caixaId);
}
