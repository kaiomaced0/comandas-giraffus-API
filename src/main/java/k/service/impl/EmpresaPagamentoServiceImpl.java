package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.EmpresaPagamentoDTO;
import k.dto.EmpresaPagamentoResponseDTO;
import k.model.EmpresaPagamento;
import k.repository.EmpresaPagamentoRepository;
import k.repository.EmpresaRepository;
import k.service.EmpresaPagamentoService;
import k.service.UsuarioLogadoService;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmpresaPagamentoServiceImpl implements EmpresaPagamentoService {

    @Inject
    EmpresaPagamentoRepository repository;

    @Inject
    EmpresaRepository empresaRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public List<EmpresaPagamentoResponseDTO> getAll() {
        return repository.findAll().stream().map(empresapagamento -> new EmpresaPagamentoResponseDTO(empresapagamento))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaPagamentoResponseDTO> getCnpj(String cnpjEmpresa) {
        return repository.findAll().stream()
                .map(empresapagamento -> new EmpresaPagamentoResponseDTO(empresapagamento))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaPagamentoResponseDTO> getEmpresa(Long idEmpresa) {
        return repository.findAll().stream()
                .map(empresapagamento -> new EmpresaPagamentoResponseDTO(empresapagamento))
                .collect(Collectors.toList());
    }

    @Override
    public EmpresaPagamentoResponseDTO getId(Long id) {
        return new EmpresaPagamentoResponseDTO(repository.findById(id));
    }

    @Override
    @Transactional
    public Response insert(EmpresaPagamentoDTO empresaPagamento) {
        EmpresaPagamento entity = EmpresaPagamentoDTO.criaEmpresaPagamento(empresaPagamento);
        repository.persist(entity);
        usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getEmpresaPagamento().add(entity);
        return Response.ok().build();
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        repository.findById(id).setAtivo(false);
        return Response.ok().build();
    }

}
