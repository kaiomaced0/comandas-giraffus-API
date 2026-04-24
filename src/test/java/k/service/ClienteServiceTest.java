package k.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

import k.AbstractServiceTest;
import k.dto.ClienteDTO;
import k.dto.ClienteResponseDTO;
import k.exception.BusinessException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ClienteServiceTest extends AbstractServiceTest {

    @Inject
    ClienteService service;

    @Test
    @Transactional
    public void insert_idempotentePorCpf() {
        Response r1 = service.insertOrFind(new ClienteDTO("11111111111", "João", "joao@test.com"));
        assertEquals(201, r1.getStatus());
        ClienteResponseDTO c1 = (ClienteResponseDTO) r1.getEntity();
        assertNotNull(c1);

        Response r2 = service.insertOrFind(new ClienteDTO("11111111111", "João", "joao@test.com"));
        assertEquals(200, r2.getStatus());
        ClienteResponseDTO c2 = (ClienteResponseDTO) r2.getEntity();
        assertEquals(c1.id(), c2.id());
    }

    @Test
    @Transactional
    public void insert_semCpf_falha() {
        assertThrows(BusinessException.class,
                () -> service.insertOrFind(new ClienteDTO(null, "Sem CPF", null)));
    }

    @Test
    @Transactional
    public void insert_atualizaNomeSeFornecidoDepois() {
        service.insertOrFind(new ClienteDTO("22222222222", null, null));
        Response r2 = service.insertOrFind(new ClienteDTO("22222222222", "Maria", null));
        ClienteResponseDTO c = (ClienteResponseDTO) r2.getEntity();
        assertEquals("Maria", c.nome());
    }
}
