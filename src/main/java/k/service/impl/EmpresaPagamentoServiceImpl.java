package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import k.dto.EmpresaPagamentoDTO;
import k.dto.EmpresaPagamentoResponseDTO;
import k.model.EmpresaPagamento;
import k.repository.EmpresaPagamentoRepository;
import k.repository.EmpresaRepository;
import k.service.EmpresaPagamentoService;

public class EmpresaPagamentoServiceImpl implements EmpresaPagamentoService {

    @Inject
    EmpresaPagamentoRepository repository;

    @Inject
    EmpresaRepository empresaRepository;

    @Override
    public List<EmpresaPagamentoResponseDTO> getAll() {
        return repository.findAll().stream().map(empresapagamento -> new EmpresaPagamentoResponseDTO(empresapagamento))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaPagamentoResponseDTO> getCnpj(String cnpjEmpresa) {
        return repository.findAll().stream()
                .filter(empresapagamento -> empresapagamento.getEmpresa() == empresaRepository.findByCnpj(cnpjEmpresa))
                .map(empresapagamento -> new EmpresaPagamentoResponseDTO(empresapagamento))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaPagamentoResponseDTO> getEmpresa(Long idEmpresa) {
        return repository.findAll().stream()
                .filter(empresapagamento -> empresapagamento.getEmpresa().getId() == idEmpresa)
                .map(empresapagamento -> new EmpresaPagamentoResponseDTO(empresapagamento))
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaPagamentoResponseDTO getId(Long id) {
        return new EmpresaPagamentoResponseDTO(repository.findById(id));
    }

    @Override
    public Response insert(EmpresaPagamentoDTO empresaPagamento) {
        EmpresaPagamento entity = EmpresaPagamentoDTO.criaEmpresaPagamento(empresaPagamento);
        entity.setEmpresa(empresaRepository.findById(empresaPagamento.idEmpresa()));
        repository.persist(entity);
        return Response.ok().build();
    }

    @Override
    public Response delete(Long id) {
        repository.findById(id).setAtivo(true);
        return Response.ok().build();
    }

}
