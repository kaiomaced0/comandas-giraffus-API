package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.TipoProdutoDTO;
import k.dto.TipoProdutoResponseDTO;
import k.model.TipoProduto;
import k.repository.EmpresaRepository;
import k.repository.TipoProdutoRepository;
import k.service.TipoProdutoService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TipoProdutoServiceImpl implements TipoProdutoService {

    @Inject
    TipoProdutoRepository repository;

    @Inject
    EmpresaRepository empresaRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public List<TipoProdutoResponseDTO> getAll() {
        return empresaRepository.findById(usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getId())
                .getTipoProdutos().stream()
                .filter(tipoProduto -> usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getTipoProdutos().contains(tipoProduto))
                .map(tipoProduto -> new TipoProdutoResponseDTO(tipoProduto.getId(), tipoProduto.getNome()))
                .collect(Collectors.toList());
    }

    @Override
    public List<TipoProdutoResponseDTO> getNome(String nome) {
        return repository.findByNome(nome).stream().filter(
                tipoProduto -> usuarioLogadoService.getPerfilUsuarioLogado()
                        .getEmpresa().getTipoProdutos().contains(tipoProduto))
                .map(tipoProduto -> new TipoProdutoResponseDTO(tipoProduto.getId(), tipoProduto.getNome()))
                .collect(Collectors.toList());
    }

    @Override
    public TipoProdutoResponseDTO getId(Long id) {
        TipoProduto entity = repository.findById(id);
        return new TipoProdutoResponseDTO(entity.getId(), entity.getNome());
    }

    @Override
    @Transactional
    public Response insert(TipoProdutoDTO tipoProduto) {
        TipoProduto entity = TipoProdutoDTO.criaTipoProduto(tipoProduto);
        usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getTipoProdutos().add(entity);
        repository.persist(entity);
        return Response.ok(new TipoProdutoResponseDTO(entity.getId(), entity.getNome())).build();
    }

    @Override
    @Transactional
    public Response update(Long idTipoProduto, TipoProdutoDTO tipoProduto) {
        try {

            TipoProduto entity = repository.findById(idTipoProduto);
            if (usuarioLogadoService.getPerfilUsuarioLogado().getEmpresa().getTipoProdutos().contains(entity)) {
                entity.setNome(tipoProduto.nome());
                return Response.ok(new TipoProdutoResponseDTO(entity.getId(), entity.getNome())).build();

            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }
    }

    @Override
    @Transactional
    public Response delete(Long id) {
        TipoProduto entity = repository.findById(id);
        entity.setAtivo(false);
        return Response.ok(new TipoProdutoResponseDTO(entity.getId(), entity.getNome())).build();
    }

}
