package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import k.dto.EmpresaDTO;
import k.dto.EmpresaResponseDTO;
import k.dto.EmpresaUpdateNomeDTO;
import k.model.Empresa;
import k.repository.EmpresaRepository;
import k.repository.UsuarioRepository;
import k.service.EmpresaService;
import k.service.UsuarioLogadoService;

public class EmpresaServiceImpl implements EmpresaService {

    @Inject
    EmpresaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    UsuarioRepository usuarioRepository;

    @Override
    public List<EmpresaResponseDTO> getAll() {
        return repository.findAll().stream().map(empresa -> new EmpresaResponseDTO(empresa))
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaResponseDTO> getNome(String nome) {
        return repository.findByNome(nome).stream().map(empresa -> new EmpresaResponseDTO(empresa))
                .collect(Collectors.toList());

    }

    @Override
    public EmpresaResponseDTO getCnpj(String cnpj) {
        return new EmpresaResponseDTO(repository.findByCnpj(cnpj));
    }

    @Override
    public EmpresaResponseDTO getId(Long id) {
        return new EmpresaResponseDTO(repository.findById(id));
    }

    @Override
    public Response insert(EmpresaDTO empresa) {
        Empresa entity = new Empresa();
        entity.setNome(empresa.nome());
        entity.setAdmin(usuarioRepository.findById(empresa.usuarioId()));
        entity.setNomeFantasia(empresa.nomeFantasia());
        entity.setCnpj(empresa.cnpj());
        entity.setComentario(empresa.comentario());
        return Response.ok(new EmpresaResponseDTO(entity)).build();
    }

    @Override
    public Response updateNome(EmpresaUpdateNomeDTO empresaUpdateNomeDTO) {
        Empresa entity = repository.findById(empresaUpdateNomeDTO.idEmpresa());
        entity.setNome(empresaUpdateNomeDTO.nome());
        return Response.ok(new EmpresaResponseDTO(entity)).build();
    }

    @Override
    public Response adicionarFuncionario(Long id) {
        usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getFuncionarios()
                .add(usuarioRepository.findById(id));
        return Response.ok().build();
    }

    @Override
    public Response removerFuncionario(Long id) {
        usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getFuncionarios()
                .remove(usuarioRepository.findById(id));
        return Response.ok().build();

    }

    @Override
    public Response inativar(Long id) {
        Empresa entity = repository.findById(id);
        entity.setAtivo(false);
        return Response.ok().build();
    }

    @Override
    public Response ativar(Long id) {
        Empresa entity = repository.findById(id);
        entity.setAtivo(true);
        return Response.ok().build();
    }

}
