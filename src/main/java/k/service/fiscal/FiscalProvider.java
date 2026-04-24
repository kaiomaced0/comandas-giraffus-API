package k.service.fiscal;

import java.util.List;

import k.model.Cliente;
import k.model.Comanda;
import k.model.DocumentoFiscal;
import k.model.Pagamento;
import k.model.enums.TipoDocumentoFiscal;

public interface FiscalProvider {

    DocumentoFiscal emitir(Comanda comanda,
                           Cliente clienteOpcional,
                           List<Pagamento> pagamentos,
                           TipoDocumentoFiscal tipo);

    void cancelar(DocumentoFiscal d);
}
