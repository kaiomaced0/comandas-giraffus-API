package k.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.EmpresaDTO;
import k.dto.EmpresaResponseDTO;
import k.dto.EmpresaUpdateNomeDTO;
import k.dto.EmpresaUpdateNomeMasterDTO;
import k.model.Caixa;
import k.model.Empresa;
import k.model.Perfil;
import k.model.Usuario;
import k.repository.CaixaRepository;
import k.repository.EmpresaRepository;
import k.repository.UsuarioRepository;
import k.service.EmpresaService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmpresaServiceImpl implements EmpresaService {

    @Inject
    EmpresaRepository repository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    CaixaRepository caixaRepository;

    @Override
    public List<EmpresaResponseDTO> getAll() {
        return repository.findAll().stream().filter(Empresa::getAtivo)
                .map(EmpresaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaResponseDTO> getAllInativos() {
        return repository.findAll().stream().filter(empresa -> !empresa.getAtivo())
                .map(EmpresaResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmpresaResponseDTO> getNome(String nome) {
        try {
            if (nome != null) {
                return repository.findByNome(nome).stream().map(empresa -> new EmpresaResponseDTO(empresa))
                        .collect(Collectors.toList());
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<EmpresaResponseDTO> getCnpj(String cnpj) {
        try {
            if (cnpj != null) {
                return repository.findByCnpj(cnpj).stream().map(empresa -> new EmpresaResponseDTO(empresa))
                        .collect(Collectors.toList());
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Response updateCaixaAtual(Long id) {
        try {

            Caixa cx = caixaRepository.findById(id);
            Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
            u.getEmpresa().setCaixaAtual(cx);
            return Response.ok().build();
        }catch (Exception e){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

    }


    @Override
    public EmpresaResponseDTO getId(Long id) {
        return new EmpresaResponseDTO(repository.findById(id));
    }

    @Override
    @Transactional
    public Response insert(EmpresaDTO empresa) {
        try {
            Empresa entity = new Empresa();
            entity.setNome(empresa.nome());
            entity.setAdmin(usuarioRepository.findById(empresa.usuarioId()));
            entity.setNomeFantasia(empresa.nomeFantasia());
            entity.setCnpj(empresa.cnpj());
            entity.setComentario(empresa.comentario());
            usuarioRepository.findById(empresa.usuarioId()).getPerfis().add(Perfil.valueOf(1));
            repository.persist(entity);
            return Response.ok(new EmpresaResponseDTO(entity)).build();

        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response updateNomeFantasia(EmpresaUpdateNomeDTO empresaUpdateNomeDTO) {
        try {
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getAdmin() == usuarioLogadoService
                    .getPerfilUsuarioLogado()) {
                Empresa entity = repository
                        .findById(usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getId());
                entity.setNomeFantasia(empresaUpdateNomeDTO.nomeFantasia());
                return Response.ok(new EmpresaResponseDTO(entity)).build();
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response removerFuncionario(Long id) {
        usuarioRepository.findById(id).setEmpresa(null);
        return Response.ok().build();

    }

    @Override
    @Transactional
    public Response inativar(Long id) {
        Empresa entity = repository.findById(id);
        entity.setAtivo(false);
        return Response.ok().build();
    }

    @Override
    @Transactional
    public Response ativar(Long id) {
        Empresa entity = repository.findById(id);
        entity.setAtivo(true);
        return Response.ok().build();
    }

    @Override
    public Response updateNomeFantasiaMaster(EmpresaUpdateNomeMasterDTO empresaUpdateNomeMasterDTO) {
        try {
            Empresa entity = repository
                    .findById(empresaUpdateNomeMasterDTO.idEmpresa());
            entity.setNomeFantasia(empresaUpdateNomeMasterDTO.nomeFantasia());
            return Response.ok(new EmpresaResponseDTO(entity)).build();

        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

}
