package k.service.fiscal.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import k.service.fiscal.FiscalProvider;

@ApplicationScoped
@Default
public class EmuladoFiscalProvider implements FiscalProvider {

    @Override
    public String getNumero() {
        return String.format("%09d", System.nanoTime() % 1_000_000_000L);
    }

    @Override
    public String getChaveAcesso() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 44);
    }

    @Override
    public String getPayloadRetorno(String numero, String chaveAcesso) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"numero\":").append(quote(numero)).append(",");
        sb.append("\"chaveAcesso\":").append(quote(chaveAcesso)).append(",");
        sb.append("\"timestamp\":").append(quote(LocalDateTime.now().toString()));
        sb.append("}");
        return sb.toString();
    }

    @Override
    public boolean isEmulado() {
        return true;
    }

    private String quote(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
