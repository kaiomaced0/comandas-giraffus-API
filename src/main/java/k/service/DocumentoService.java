package k.service;

import java.util.List;

import k.dto.DocumentoResponseDTO;
import k.dto.DocumentoUrlDTO;

public interface DocumentoService {

    DocumentoResponseDTO upload(String tipoStr, String nomeOriginal, String contentType, byte[] bytes);

    List<DocumentoResponseDTO> listar();

    DocumentoUrlDTO urlTemporaria(Long id);

    void excluir(Long id);
}
