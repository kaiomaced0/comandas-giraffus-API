package k.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import k.dto.DashboardKpisDTO;
import k.model.Comanda;
import k.model.Empresa;
import k.model.Pagamento;
import k.model.Usuario;
import k.repository.ComandaRepository;
import k.repository.PagamentoRepository;
import k.service.DashboardService;
import k.service.UsuarioLogadoService;

import org.jboss.logging.Logger;

@ApplicationScoped
public class DashboardServiceImpl implements DashboardService {

    private static final Logger LOG = Logger.getLogger(DashboardServiceImpl.class);

    private static final DashboardKpisDTO ZERADO = new DashboardKpisDTO(
            BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
            0L,
            BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
            BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
            0L,
            0L);

    @Inject
    PagamentoRepository pagamentoRepository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public DashboardKpisDTO kpis(LocalDate from, LocalDate to) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            return ZERADO;
        }
        Empresa empresa = logado.getEmpresa();

        LocalDateTime fromTs = from == null ? null : from.atStartOfDay();
        LocalDateTime toTs = to == null ? null : to.atTime(LocalTime.MAX);

        // ---- Pagamentos (faturamento, número de pagamentos, gorjetas) ----
        List<Pagamento> pagamentos = pagamentoRepository.findParaRelatorio(empresa, fromTs, toTs);

        BigDecimal faturamento = pagamentos.stream()
                .map(DashboardServiceImpl::valorPagamento)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long numPagamentos = pagamentos.size();

        BigDecimal totalGorjetas = pagamentos.stream()
                .map(p -> p.getValorGorjeta() == null
                        ? BigDecimal.ZERO
                        : BigDecimal.valueOf(p.getValorGorjeta()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ---- Comandas (abertas / finalizadas no período) ----
        List<Long> idsEmpresa = empresa.getComandas() == null
                ? List.of()
                : empresa.getComandas().stream()
                        .filter(c -> c != null && c.getId() != null)
                        .map(Comanda::getId)
                        .collect(Collectors.toList());

        List<Comanda> comandas = comandaRepository.findParaRelatorio(idsEmpresa, fromTs, toTs);

        long comandasAbertas = comandas.stream()
                .filter(c -> !Boolean.TRUE.equals(c.getFinalizada()))
                .count();
        long comandasFinalizadas = comandas.stream()
                .filter(c -> Boolean.TRUE.equals(c.getFinalizada()))
                .count();

        BigDecimal ticketMedio = comandasFinalizadas == 0
                ? BigDecimal.ZERO
                : faturamento.divide(BigDecimal.valueOf(comandasFinalizadas), 2, RoundingMode.HALF_UP);

        LOG.info("Dashboard.kpis empresa=" + empresa.getId()
                + " faturamento=" + faturamento
                + " numPagamentos=" + numPagamentos);

        return new DashboardKpisDTO(
                faturamento.setScale(2, RoundingMode.HALF_UP),
                numPagamentos,
                ticketMedio.setScale(2, RoundingMode.HALF_UP),
                totalGorjetas.setScale(2, RoundingMode.HALF_UP),
                comandasAbertas,
                comandasFinalizadas);
    }

    private static BigDecimal valorPagamento(Pagamento p) {
        if (p.getValorTotal() != null) {
            return p.getValorTotal();
        }
        return p.getValorPagamento() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(p.getValorPagamento());
    }

}
