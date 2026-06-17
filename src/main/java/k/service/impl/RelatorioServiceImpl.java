package k.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import k.dto.FaturamentoPorFormaDTO;
import k.dto.TopProdutoDTO;
import k.dto.VendaPorDiaDTO;
import k.model.Comanda;
import k.model.Empresa;
import k.model.FormaPagamento;
import k.model.ItemCompra;
import k.model.Pagamento;
import k.model.Produto;
import k.model.Usuario;
import k.repository.ComandaRepository;
import k.repository.ItemCompraRepository;
import k.repository.PagamentoRepository;
import k.service.RelatorioService;
import k.service.UsuarioLogadoService;

import org.jboss.logging.Logger;

@ApplicationScoped
public class RelatorioServiceImpl implements RelatorioService {

    private static final Logger LOG = Logger.getLogger(RelatorioServiceImpl.class);

    @Inject
    PagamentoRepository pagamentoRepository;

    @Inject
    ComandaRepository comandaRepository;

    @Inject
    ItemCompraRepository itemCompraRepository;

    @Inject
    UsuarioLogadoService usuarioLogadoService;

    @Override
    public List<FaturamentoPorFormaDTO> faturamentoPorForma(LocalDate from, LocalDate to) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            return List.of();
        }
        Empresa empresa = logado.getEmpresa();
        LocalDateTime fromTs = from == null ? null : from.atStartOfDay();
        LocalDateTime toTs = to == null ? null : to.atTime(LocalTime.MAX);

        List<Pagamento> pagamentos = pagamentoRepository.findParaRelatorio(empresa, fromTs, toTs);

        // Agrupa por forma de pagamento (em Java).
        Map<FormaPagamento, List<Pagamento>> porForma = pagamentos.stream()
                .filter(p -> p.getFormaPagamento() != null)
                .collect(Collectors.groupingBy(Pagamento::getFormaPagamento, LinkedHashMap::new, Collectors.toList()));

        List<FaturamentoPorFormaDTO> resultado = new ArrayList<>();
        for (Map.Entry<FormaPagamento, List<Pagamento>> e : porForma.entrySet()) {
            FormaPagamento forma = e.getKey();
            List<Pagamento> lista = e.getValue();
            BigDecimal total = lista.stream()
                    .map(RelatorioServiceImpl::valorPagamento)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            resultado.add(new FaturamentoPorFormaDTO(
                    forma.getId(),
                    forma.getLabel(),
                    lista.size(),
                    total));
        }
        resultado.sort(Comparator.comparing(FaturamentoPorFormaDTO::formaPagamentoId));
        return resultado;
    }

    @Override
    public List<VendaPorDiaDTO> vendasPorDia(LocalDate from, LocalDate to) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            return List.of();
        }
        Empresa empresa = logado.getEmpresa();
        LocalDateTime fromTs = from == null ? null : from.atStartOfDay();
        LocalDateTime toTs = to == null ? null : to.atTime(LocalTime.MAX);

        List<Pagamento> pagamentos = pagamentoRepository.findParaRelatorio(empresa, fromTs, toTs);

        // Agrupa por dia (dataInclusao -> LocalDate), em Java.
        Map<LocalDate, List<Pagamento>> porDia = pagamentos.stream()
                .filter(p -> p.getDataInclusao() != null)
                .collect(Collectors.groupingBy(p -> p.getDataInclusao().toLocalDate(),
                        LinkedHashMap::new, Collectors.toList()));

        List<VendaPorDiaDTO> resultado = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Pagamento>> e : porDia.entrySet()) {
            BigDecimal total = e.getValue().stream()
                    .map(RelatorioServiceImpl::valorPagamento)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            resultado.add(new VendaPorDiaDTO(e.getKey(), e.getValue().size(), total));
        }
        resultado.sort(Comparator.comparing(VendaPorDiaDTO::dia));
        return resultado;
    }

    @Override
    public List<TopProdutoDTO> topProdutos(LocalDate from, LocalDate to, int limit) {
        Usuario logado = usuarioLogadoService.getPerfilUsuarioLogado();
        if (logado == null || logado.getEmpresa() == null) {
            return List.of();
        }
        Empresa empresa = logado.getEmpresa();
        LocalDateTime fromTs = from == null ? null : from.atStartOfDay();
        LocalDateTime toTs = to == null ? null : to.atTime(LocalTime.MAX);

        List<Long> idsEmpresa = empresa.getComandas() == null
                ? List.of()
                : empresa.getComandas().stream()
                        .filter(c -> c != null && c.getId() != null)
                        .map(Comanda::getId)
                        .collect(Collectors.toList());

        List<ItemCompra> itens = itemCompraRepository.findParaRelatorio(idsEmpresa, fromTs, toTs);

        // Agrupa por produto, em Java.
        Map<Produto, List<ItemCompra>> porProduto = itens.stream()
                .filter(i -> i.getProduto() != null)
                .collect(Collectors.groupingBy(ItemCompra::getProduto, LinkedHashMap::new, Collectors.toList()));

        List<TopProdutoDTO> resultado = new ArrayList<>();
        for (Map.Entry<Produto, List<ItemCompra>> e : porProduto.entrySet()) {
            Produto produto = e.getKey();
            long quantidade = e.getValue().stream()
                    .mapToLong(i -> i.getQuantidade() == null ? 0L : i.getQuantidade().longValue())
                    .sum();
            BigDecimal receita = e.getValue().stream()
                    .map(i -> {
                        int qtd = i.getQuantidade() == null ? 0 : i.getQuantidade();
                        BigDecimal preco = i.getPreco() == null
                                ? BigDecimal.ZERO
                                : BigDecimal.valueOf(i.getPreco());
                        return preco.multiply(BigDecimal.valueOf(qtd));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
            resultado.add(new TopProdutoDTO(produto.getId(), produto.getNome(), quantidade, receita));
        }

        // Ordena desc por quantidade e limita.
        resultado.sort(Comparator.comparingLong(TopProdutoDTO::quantidade).reversed());
        int max = Math.max(0, limit);
        if (resultado.size() > max) {
            return new ArrayList<>(resultado.subList(0, max));
        }
        return resultado;
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
