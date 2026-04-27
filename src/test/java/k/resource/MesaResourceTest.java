package k.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

/**
 * Smoke test REST do MesaResource.
 *
 * DISABLED neste ambiente: JDK 25 + Netty (versao trazida pelo Quarkus 3.2.0)
 * no Windows quebram o boot do Quarkus em forks de teste com:
 *   java.io.IOException: Unable to establish loopback connection
 *     at sun.nio.ch.PipeImpl$Initializer.init
 *     at sun.nio.ch.WEPollSelectorImpl.<init>
 *     at io.netty.channel.nio.NioEventLoop.openSelector
 * Causa raiz: sun.nio.ch.UnixDomainSockets.connect0 retornando EINVAL sob
 * concorrencia (reproduzivel ate sem Quarkus, em teste com varios Selector.open()).
 *
 * Para reabilitar: rodar com JDK 21 (LTS) ou aguardar JDK 25 com Netty atualizado.
 * Tambem requer cobertura de @TestSecurity / token mock para validar 200/201
 * em endpoints protegidos por @RolesAllowed.
 */
@QuarkusTest
@Disabled("@QuarkusTest indisponivel: JDK 25 + Netty/Vertx travam no boot no Windows (UnixDomainSockets.connect0 EINVAL)")
class MesaResourceTest {

    @Test
    void getMesaSemTokenDeve401() {
        given()
            .when().get("/mesa")
            .then()
            .statusCode(anyOf(is(401), is(403)));
    }
}
