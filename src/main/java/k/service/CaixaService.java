package k.service;

import java.util.List;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import k.dto.CaixaAbrirInputDTO;
import k.dto.CaixaDTO;
import k.dto.CaixaFecharInputDTO;
import k.dto.CaixaResponseDTO;

public interface CaixaService {
    public List<CaixaResponseDTO> getAll();

    public CaixaResponseDTO getCaixaAtual();

    public List<CaixaResponseDTO> getAllFechadas();

    public CaixaResponseDTO getId(@PathParam("id") Long id);

    public Response insert(CaixaDTO caixa);

    public Response delete(@PathParam("id") Long id);

    @Deprecated
    public Response fechar(@PathParam("id") Long id);

    // Onda E - novos métodos (caixa por usuário)
    public CaixaResponseDTO abrir(CaixaAbrirInputDTO dto);

    public CaixaResponseDTO fechar(Long id, CaixaFecharInputDTO dto);

    public CaixaResponseDTO meuCaixaAberto();

    public List<CaixaResponseDTO> abertosNaEmpresa();

    public CaixaResponseDTO fecharForcado(Long id, CaixaFecharInputDTO dto, String justificativa);
}
