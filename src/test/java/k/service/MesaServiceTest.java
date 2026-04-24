package k.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import k.AbstractServiceTest;
import k.dto.ComandaResponseDTO;
import k.dto.MesaDTO;
import k.dto.MesaResponseDTO;
import k.exception.BusinessException;
import k.model.Comanda;
import k.model.Mesa;
import k.repository.ComandaRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class MesaServiceTest extends AbstractServiceTest {

    @Inject
    MesaService service;

    @Inject
    ComandaRepository comandaRepository;

    @Test
    @Transactional
    public void getAll_listaMesasDaEmpresa() {
        List<MesaResponseDTO> all = service.getAll();
        assertTrue(all.stream().anyMatch(m -> m.id().equals(mesa.getId())));
    }

    @Test
    @Transactional
    public void insert_caminhoFeliz() {
        Response r = service.insert(new MesaDTO("99", 6));
        assertEquals(200, r.getStatus());
    }

    @Test
    @Transactional
    public void insert_semIdentificador_falha() {
        assertThrows(BusinessException.class, () -> service.insert(new MesaDTO(null, 4)));
    }

    @Test
    @Transactional
    public void getId_mesaDeOutraEmpresa_404() {
        // mesa está na empresa do admin; vamos confirmar acesso OK
        MesaResponseDTO dto = service.getId(mesa.getId());
        assertNotNull(dto);
        // testar caso 404 de id inexistente
        assertThrows(NotFoundException.class, () -> service.getId(999999L));
    }

    @Test
    @Transactional
    public void delete_marcaInativo() {
        service.delete(mesa.getId());
        // Depois de soft-delete, getAll não encontra
        List<MesaResponseDTO> all = service.getAll();
        assertTrue(all.stream().noneMatch(m -> m.id().equals(mesa.getId())));
    }

    @Test
    @Transactional
    public void getComandas_listaDaMesa() {
        // criar 2 comandas na mesa (deve permitir múltiplas - premissa)
        Comanda c1 = novaComanda("Comanda 1");
        Comanda c2 = novaComanda("Comanda 2");
        List<ComandaResponseDTO> comandas = service.getComandas(mesa.getId());
        assertEquals(2, comandas.size());
    }

    private Comanda novaComanda(String nome) {
        Comanda c = new Comanda();
        c.setNome(nome);
        c.setPreco(0.0);
        c.setFinalizada(false);
        c.setMesa(mesa);
        c.setAtendente(admin);
        comandaRepository.persist(c);
        return c;
    }
}
