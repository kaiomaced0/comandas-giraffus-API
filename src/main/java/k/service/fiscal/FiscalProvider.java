package k.service.fiscal;

public interface FiscalProvider {
    String getNumero();

    String getChaveAcesso();

    String getPayloadRetorno(String numero, String chaveAcesso);

    boolean isEmulado();
}
