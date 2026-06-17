package k.service;

import java.time.LocalDate;
import java.util.List;

import k.dto.FaturamentoPorFormaDTO;
import k.dto.TopProdutoDTO;
import k.dto.VendaPorDiaDTO;

public interface RelatorioService {

    List<FaturamentoPorFormaDTO> faturamentoPorForma(LocalDate from, LocalDate to);

    List<VendaPorDiaDTO> vendasPorDia(LocalDate from, LocalDate to);

    List<TopProdutoDTO> topProdutos(LocalDate from, LocalDate to, int limit);

}
