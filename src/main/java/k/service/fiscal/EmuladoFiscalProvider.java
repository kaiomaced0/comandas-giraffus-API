package k.service.fiscal;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import k.model.Cliente;
import k.model.Comanda;
import k.model.DocumentoFiscal;
import k.model.Pagamento;
import k.model.enums.StatusEmissaoFiscal;
import k.model.enums.TipoDocumentoFiscal;

/**
 * Implementação emulada do FiscalProvider: gera números fictícios sem chamar ERP.
 * Será substituída/complementada futuramente por um ErpFiscalProvider real.
 */
@ApplicationScoped
public class EmuladoFiscalProvider implements FiscalProvider {

    @Override
    public DocumentoFiscal emitir(Comanda comanda,
                                  Cliente clienteOpcional,
                                  List<Pagamento> pagamentos,
                                  TipoDocumentoFiscal tipo) {
        DocumentoFiscal d = new DocumentoFiscal();
        d.setTipo(tipo == null ? TipoDocumentoFiscal.NFCE : tipo);
        d.setComanda(comanda);
        d.setCliente(clienteOpcional);
        d.setPagamentos(pagamentos);
        String uid = UUID.randomUUID().toString();
        d.setNumero("EMU-" + uid.substring(0, 8).toUpperCase());
        d.setChaveAcesso("EMU" + uid.replace("-", "").toUpperCase());
        d.setStatusEmissao(StatusEmissaoFiscal.EMITIDO);
        d.setEmulado(true);
        d.setEmitidoEm(LocalDateTime.now());
        d.setPayloadEmissao("{\"emulado\":true}");
        d.setPayloadRetorno("{\"numero\":\"" + d.getNumero() + "\"}");
        return d;
    }

    @Override
    public void cancelar(DocumentoFiscal d) {
        d.setStatusEmissao(StatusEmissaoFiscal.CANCELADO);
        d.setPayloadRetorno(d.getPayloadRetorno() == null
                ? "{\"cancelado\":true}"
                : d.getPayloadRetorno() + ",{\"cancelado\":true}");
    }
}
