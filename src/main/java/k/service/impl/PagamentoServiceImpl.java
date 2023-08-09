package k.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import k.dto.PagamentoDTO;
import k.dto.PagamentoResponseDTO;
import k.model.Pagamento;
import k.model.PagamentoRemovidoHistorico;
import k.repository.ComandaRepository;
import k.repository.PagamentoRemovidoHistoricoRepository;
import k.repository.PagamentoRepository;
import k.repository.UsuarioRepository;
import k.service.PagamentoService;

public class PagamentoServiceImpl implements PagamentoService {

    @Inject
    PagamentoRepository repository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    PagamentoRemovidoHistoricoRepository pagamentoRemovidoHistoricoRepository;

    @Override
    public List<PagamentoResponseDTO> getAll() {
        return repository.findAll().stream()
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
    public Response insert(PagamentoDTO pagamentoDTO) {
        Pagamento entity = new Pagamento();
        entity.setComanda(comandaRepository.findById(pagamentoDTO.idComanda()));
        entity.setFormaPagamento(pagamentoDTO.formaPagamento());
        entity.setUsuarioCaixa(usuarioRepository.findById(pagamentoDTO.idUsuarioCaixa()));
        entity.setValorPagamento(pagamentoDTO.valorPagamento());
        entity.getComanda().setTaxaServico(true);
        return Response.ok().build();

    }

    @Override
    public Response delete(Long id, String observacao) {
        Pagamento pagamento = repository.findById(id);
        pagamento.setAtivo(false);
        PagamentoRemovidoHistorico pagamentoRemovido = new PagamentoRemovidoHistorico();
        pagamentoRemovido.setComentario(observacao);
        pagamentoRemovidoHistoricoRepository.persist(pagamentoRemovido);
        return Response.ok().build();
    }

}
