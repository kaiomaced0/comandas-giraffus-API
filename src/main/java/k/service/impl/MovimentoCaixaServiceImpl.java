package k.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import k.dto.MovimentoCaixaDTO;
import k.dto.MovimentoCaixaResponseDTO;
import k.exception.BusinessException;
import k.model.Caixa;
import k.model.MovimentoCaixa;
import k.model.Usuario;
import k.model.enums.TipoMovimentoCaixa;
import k.repository.CaixaRepository;
import k.repository.MovimentoCaixaRepository;
import k.service.MovimentoCaixaService;
import k.service.UsuarioLogadoService;

@ApplicationScoped
public class MovimentoCaixaServiceImpl implements MovimentoCaixaService {

    @Inject
    MovimentoCaixaRepository repository;

    @Inject
    CaixaRepository caixaRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    @Transactional
    public Response sangria(Long idCaixa, MovimentoCaixaDTO dto) {
        return registrar(idCaixa, null, dto, TipoMovimentoCaixa.SANGRIA);
    }

    @Override
    @Transactional
    public Response suprimento(Long idCaixa, MovimentoCaixaDTO dto) {
        return registrar(idCaixa, null, dto, TipoMovimentoCaixa.SUPRIMENTO);
    }

    @Override
    @Transactional
    public Response transferir(Long idCaixaOrigem, Long idCaixaDestino, MovimentoCaixaDTO dto) {
        if (idCaixaDestino == null || idCaixaDestino.equals(idCaixaOrigem)) {
            throw new BusinessException("Caixa de destino inválido");
        }
        return registrar(idCaixaOrigem, idCaixaDestino, dto, TipoMovimentoCaixa.TRANSFERENCIA);
    }

    private Response registrar(Long idCaixa, Long idCaixaDestino, MovimentoCaixaDTO dto,
            TipoMovimentoCaixa tipo) {
        if (dto == null || dto.valor() == null) {
            throw new BusinessException("valor é obrigatório");
        }
        if (dto.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("valor deve ser maior que zero");
        }
        Usuario u = usuarioLogadoService.getPerfilUsuarioLogado();
        Caixa caixa = caixaRepository.findById(idCaixa);
        if (caixa == null || caixa.getEmpresa() == null
                || !caixa.getEmpresa().getId().equals(u.getEmpresa().getId())) {
            throw new NotFoundException("Caixa origem não encontrado");
        }
        if (Boolean.TRUE.equals(caixa.getFechado())) {
            throw new BusinessException("Não é permitido movimentar caixa fechado");
        }
        MovimentoCaixa m = new MovimentoCaixa();
        m.setTipo(tipo);
        m.setCaixa(caixa);
        m.setValor(dto.valor());
        m.setMotivo(dto.motivo());
        m.setUsuario(u);
        m.setData(LocalDateTime.now());
        if (idCaixaDestino != null) {
            Caixa destino = caixaRepository.findById(idCaixaDestino);
            if (destino == null || destino.getEmpresa() == null
                    || !destino.getEmpresa().getId().equals(u.getEmpresa().getId())) {
                throw new NotFoundException("Caixa destino não encontrado");
            }
            if (Boolean.TRUE.equals(destino.getFechado())) {
                throw new BusinessException("Caixa destino está fechado");
            }
            m.setCaixaDestino(destino);
        }
        repository.persist(m);
        return Response.ok(new MovimentoCaixaResponseDTO(m)).build();
    }
}
