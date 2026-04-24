package k.service;

import jakarta.ws.rs.core.Response;
import k.dto.MovimentoCaixaDTO;

public interface MovimentoCaixaService {

    Response sangria(Long idCaixa, MovimentoCaixaDTO dto);

    Response suprimento(Long idCaixa, MovimentoCaixaDTO dto);

    Response transferir(Long idCaixaOrigem, Long idCaixaDestino, MovimentoCaixaDTO dto);
}
