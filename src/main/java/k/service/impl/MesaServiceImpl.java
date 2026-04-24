package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import k.dto.ComandaResponseDTO;
import k.dto.MesaDTO;
import k.dto.MesaResponseDTO;
import k.exception.BusinessException;
import k.model.Mesa;
import k.model.Usuario;
import k.repository.ComandaRepository;
import k.repository.MesaRepository;
import k.service.MesaService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class MesaServiceImpl implements MesaService {

    @Inject
    MesaRepository repository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public List<MesaResponseDTO> getAll() {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        return repository.findByEmpresa(u.getEmpresa()).stream()
                .map(MesaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public MesaResponseDTO getId(Long id) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Mesa mesa = repository.findById(id);
        if (mesa == null || mesa.getEmpresa() == null
                || !mesa.getEmpresa().getId().equals(u.getEmpresa().getId())) {
            throw new NotFoundException("Mesa não encontrada");
        }
        return new MesaResponseDTO(mesa);
    }

    @Override
    @Transactional
    public Response insert(MesaDTO dto) {
        if (dto == null || dto.identificador() == null || dto.identificador().isBlank()) {
            throw new BusinessException("identificador é obrigatório");
        }
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Mesa mesa = new Mesa();
        mesa.setEmpresa(u.getEmpresa());
        mesa.setIdentificador(dto.identificador());
        mesa.setCapacidade(dto.capacidade());
        repository.persist(mesa);
        return Response.ok(new MesaResponseDTO(mesa)).build();
    }

    @Override
    @Transactional
    public Response update(Long id, MesaDTO dto) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Mesa mesa = repository.findById(id);
        if (mesa == null || mesa.getEmpresa() == null
                || !mesa.getEmpresa().getId().equals(u.getEmpresa().getId())) {
            throw new NotFoundException("Mesa não encontrada");
        }
        if (dto.identificador() != null && !dto.identificador().isBlank()) {
            mesa.setIdentificador(dto.identificador());
        }
        if (dto.capacidade() != null) {
            mesa.setCapacidade(dto.capacidade());
        }
        return Response.ok(new MesaResponseDTO(mesa)).build();
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Mesa mesa = repository.findById(id);
        if (mesa == null || mesa.getEmpresa() == null
                || !mesa.getEmpresa().getId().equals(u.getEmpresa().getId())) {
            throw new NotFoundException("Mesa não encontrada");
        }
        mesa.setAtivo(false);
        return Response.ok().build();
    }

    @Override
    public List<ComandaResponseDTO> getComandas(Long idMesa) {
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Mesa mesa = repository.findById(idMesa);
        if (mesa == null || mesa.getEmpresa() == null
                || !mesa.getEmpresa().getId().equals(u.getEmpresa().getId())) {
            throw new NotFoundException("Mesa não encontrada");
        }
        return comandaRepository.find("mesa = ?1", mesa).list().stream()
                .map(ComandaResponseDTO::new)
                .collect(Collectors.toList());
    }
}
