package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import k.dto.PagamentoDTO;
import k.dto.PagamentoDeleteDTO;
import k.dto.PagamentoResponseDTO;
import k.model.FormaPagamento;
import k.model.Pagamento;
import k.model.PagamentoRemovidoHistorico;
import k.repository.ComandaRepository;
import k.repository.PagamentoRemovidoHistoricoRepository;
import k.repository.PagamentoRepository;
import k.repository.UsuarioRepository;
import k.service.PagamentoService;
import k.service.UsuarioLogadoService;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PagamentoServiceImpl implements PagamentoService {

    @Inject
    PagamentoRepository repository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Inject
    PagamentoRemovidoHistoricoRepository pagamentoRemovidoHistoricoRepository;

    @Override
    public List<PagamentoResponseDTO> getAll() {
        return repository.findAll().stream()
                .filter(pagamento -> pagamento.getUsuarioCaixa().getEmpresa() == usuarioLogadoService
                        .getPerfilUsuarioLogado().getEmpresa())
                .filter(pagamento -> pagamento.getAtivo() == true)
                .map(pagamento -> new PagamentoResponseDTO(pagamento.getComanda().getId(),
                        pagamento.getPagamentoRealizado(), pagamento.getFormaPagamento(),
                        pagamento.getUsuarioCaixa().getId(),
                        pagamento.getValorPagamento()))
                .collect(Collectors.toList());
    }

    @Override
    public PagamentoResponseDTO getId(Long id) {
        Pagamento entity = repository.findById(id);
        return new PagamentoResponseDTO(entity.getComanda().getId(), entity.getPagamentoRealizado(),
                entity.getFormaPagamento(),
                entity.getUsuarioCaixa().getId(), entity.getValorPagamento());
    }

    @Override
    @Transactional
    public Response insert(PagamentoDTO pagamentoDTO) {
        try {
            Pagamento entity = new Pagamento();
            entity.setComanda(comandaRepository.findById(pagamentoDTO.idComanda()));
            entity.setFormaPagamento(FormaPagamento.valueOf(pagamentoDTO.idFormaPagamento()));
            entity.setUsuarioCaixa(usuarioRepository.findById(usuarioLogadoService.getPerfilUsuarioLogado().getId()));
            entity.setValorPagamento(pagamentoDTO.valorPagamento());
            if (entity.getValorPagamento() < entity.getComanda().getPreco()) {
                throw new Exception();
            }
            entity.setPagamentoRealizado(true);
            entity.getComanda().setFinalizada(true);
            entity.setValorGorjeta(entity.getComanda().getPreco() - entity.getValorPagamento());
            if (entity.getValorGorjeta() > entity.getValorPagamento() * 0.01) {
                entity.getComanda().setTaxaServico(true);
            }
            return Response.ok().build();
        } catch (Exception e) {
            return Response.status(Status.STATUS_NO_TRANSACTION).build();
        }

    }

    @Override
    @Transactional
    public Response delete(PagamentoDeleteDTO pagamentoDeleteDTO) {
        Pagamento pagamento = repository.findById(pagamentoDeleteDTO.id());
        pagamento.setAtivo(false);
        PagamentoRemovidoHistorico pagamentoRemovido = new PagamentoRemovidoHistorico();
        pagamentoRemovido.setComentario(pagamentoDeleteDTO.observacao());
        pagamentoRemovidoHistoricoRepository.persist(pagamentoRemovido);
        return Response.ok().build();
    }

}
