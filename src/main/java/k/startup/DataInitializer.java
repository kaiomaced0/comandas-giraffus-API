package k.startup;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import k.model.AmbienteGateway;
import k.model.Caixa;
import k.model.Cliente;
import k.model.Comanda;
import k.model.DocumentoFiscal;
import k.model.Empresa;
import k.model.EmpresaGatewayConfig;
import k.model.FormaPagamento;
import k.model.ItemCompra;
import k.model.Mesa;
import k.model.ModoPagamento;
import k.model.MovimentoCaixa;
import k.model.Pagamento;
import k.model.PagamentoItem;
import k.model.Pedido;
import k.model.Perfil;
import k.model.Produto;
import k.model.StatusDocumentoFiscal;
import k.model.StatusPedido;
import k.model.TipoDocumentoFiscal;
import k.model.TipoGateway;
import k.model.TipoMovimentoCaixa;
import k.model.TipoProduto;
import k.model.Usuario;
import k.service.HashService;

import org.jboss.logging.Logger;

@ApplicationScoped
public class DataInitializer {

    private static final Logger LOG = Logger.getLogger(DataInitializer.class);

    private static final String IMG_HAMBURGUER = "https://firebasestorage.googleapis.com/v0/b/teste-8ed63.appspot.com/o/hamburguerdefault.png?alt=media&token=95d84ca1-0a80-4e3e-a771-6697808f85f4";
    private static final String IMG_BEBIDA = "https://firebasestorage.googleapis.com/v0/b/teste-8ed63.appspot.com/o/bebidasdefault.png?alt=media&token=6342f7f6-0e05-4587-bc77-6f863cfd940a";

    @Inject
    HashService hashService;

    @Inject
    EntityManager em;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        Long count = (Long) em.createQuery("SELECT COUNT(u) FROM Usuario u").getSingleResult();
        if (count > 0) {
            LOG.info("Banco ja possui dados, pulando inicializacao.");
            return;
        }

        LOG.info("Inicializando dados de desenvolvimento...");

        String senhaHash = hashService.getHashSenha("123456");

        // ==================== Usuarios ====================
        Usuario admin = criarUsuario("Administrador do Sistema", "admin@gmail.com", "admin", null, senhaHash);

        // Admins das empresas
        Usuario anaSilva         = criarUsuario("AnaSilva",         "AnaSilva@dominio.com",         "AnaSilva",         "0000000", senhaHash);
        Usuario brunoCastro      = criarUsuario("BrunoCastro",      "BrunoCastro@dominio.com",      "BrunoCastro",      "0000000", senhaHash);
        Usuario carlaDias        = criarUsuario("CarlaDias",        "CarlaDias@dominio.com",        "CarlaDias",        "0000000", senhaHash);
        Usuario zoeFernandes     = criarUsuario("ZoeFernandes",     "ZoeFernandes@dominio.com",     "ZoeFernandes",     "0000000", senhaHash);
        Usuario liamSantos       = criarUsuario("LiamSantos",       "LiamSantos@dominio.com",       "LiamSantos",       "0000000", senhaHash);
        Usuario oliviaPereira    = criarUsuario("OliviaPereira",    "OliviaPereira@dominio.com",    "OliviaPereira",    "0000000", senhaHash);
        Usuario noahRodrigues    = criarUsuario("NoahRodrigues",    "NoahRodrigues@dominio.com",    "NoahRodrigues",    "0000000", senhaHash);
        Usuario emmaFerreira     = criarUsuario("EmmaFerreira",     "EmmaFerreira@dominio.com",     "EmmaFerreira",     "0000000", senhaHash);
        Usuario isabellaGomes    = criarUsuario("IsabellaGomes",    "IsabellaGomes@dominio.com",    "IsabellaGomes",    "0000000", senhaHash);

        // Garcons / Caixa / Cozinha
        Usuario sophiaMartins    = criarUsuario("SophiaMartins",    "SophiaMartins@dominio.com",    "SophiaMartins",    "0000000", senhaHash);
        Usuario avaOliveira      = criarUsuario("AvaOliveira",      "AvaOliveira@dominio.com",      "AvaOliveira",      "0000000", senhaHash);
        Usuario charlotteSousa   = criarUsuario("CharlotteSousa",   "CharlotteSousa@dominio.com",   "CharlotteSousa",   "0000000", senhaHash);
        Usuario miaFernandes     = criarUsuario("MiaFernandes",     "MiaFernandes@dominio.com",     "MiaFernandes",     "0000000", senhaHash);
        Usuario ameliaRibeiro    = criarUsuario("AmeliaRibeiro",    "AmeliaRibeiro@dominio.com",    "AmeliaRibeiro",    "0000000", senhaHash);
        Usuario harperCarvalho   = criarUsuario("HarperCarvalho",   "HarperCarvalho@dominio.com",   "HarperCarvalho",   "0000000", senhaHash);
        Usuario evelynAlves      = criarUsuario("EvelynAlves",      "EvelynAlves@dominio.com",      "EvelynAlves",      "0000000", senhaHash);
        Usuario liamSantos2      = criarUsuario("LiamSantos2",      "LiamSantos2@dominio.com",      "LiamSantos2",      "0000000", senhaHash);
        Usuario jamesFreitas     = criarUsuario("JamesFreitas",     "JamesFreitas@dominio.com",     "JamesFreitas",     "0000000", senhaHash);
        Usuario benjaminMendes   = criarUsuario("BenjaminMendes",   "BenjaminMendes@dominio.com",   "BenjaminMendes",   "0000000", senhaHash);
        Usuario elijahAraujo     = criarUsuario("ElijahAraujo",     "ElijahAraujo@dominio.com",     "ElijahAraujo",     "0000000", senhaHash);

        // Usuarios extras (caixa)
        Usuario lucasFerreira    = criarUsuario("LucasFerreira",    "LucasFerreira@dominio.com",    "LucasFerreira",    "0000000", senhaHash);
        Usuario alexanderCorreia = criarUsuario("AlexanderCorreia", "AlexanderCorreia@dominio.com", "AlexanderCorreia", "0000000", senhaHash);
        Usuario danielSousa      = criarUsuario("DanielSousa",      "DanielSousa@dominio.com",      "DanielSousa",      "0000000", senhaHash);
        Usuario loganNascimento  = criarUsuario("LoganNascimento",  "LoganNascimento@dominio.com",  "LoganNascimento",  "0000000", senhaHash);
        Usuario sebastianRocha   = criarUsuario("SebastianRocha",   "SebastianRocha@dominio.com",   "SebastianRocha",   "0000000", senhaHash);
        Usuario masonLima        = criarUsuario("MasonLima",        "MasonLima@dominio.com",        "MasonLima",        "0000000", senhaHash);
        Usuario ellaFerreira     = criarUsuario("EllaFerreira",     "EllaFerreira@dominio.com",     "EllaFerreira",     "0000000", senhaHash);
        Usuario graysonBarbosa   = criarUsuario("GraysonBarbosa",   "GraysonBarbosa@dominio.com",   "GraysonBarbosa",   "0000000", senhaHash);
        Usuario jackLopes        = criarUsuario("JackLopes",        "JackLopes@dominio.com",        "JackLopes",        "0000000", senhaHash);
        Usuario leviCardoso      = criarUsuario("LeviCardoso",      "LeviCardoso@dominio.com",      "LeviCardoso",      "0000000", senhaHash);
        Usuario milaGarcia       = criarUsuario("MilaGarcia",       "MilaGarcia@dominio.com",       "MilaGarcia",       "0000000", senhaHash);
        Usuario asherCosta       = criarUsuario("AsherCosta",       "AsherCosta@dominio.com",       "AsherCosta",       "0000000", senhaHash);
        Usuario leoFonseca       = criarUsuario("LeoFonseca",       "LeoFonseca@dominio.com",       "LeoFonseca",       "0000000", senhaHash);
        Usuario hazelPinto       = criarUsuario("HazelPinto",       "HazelPinto@dominio.com",       "HazelPinto",       "0000100", senhaHash);

        // ==================== Perfis ====================
        admin.setPerfis(Set.of(Perfil.MASTER, Perfil.ADMIN));

        List<Usuario> adminsEmpresa = List.of(
                anaSilva, brunoCastro, carlaDias, zoeFernandes, liamSantos,
                oliviaPereira, noahRodrigues, emmaFerreira, isabellaGomes);
        adminsEmpresa.forEach(u -> u.setPerfis(Set.of(Perfil.ADMIN)));

        List<Usuario> garcons = List.of(
                sophiaMartins, avaOliveira, charlotteSousa, miaFernandes,
                ameliaRibeiro, harperCarvalho, evelynAlves, liamSantos2,
                jamesFreitas, benjaminMendes, elijahAraujo);
        garcons.forEach(u -> u.setPerfis(Set.of(Perfil.GARCOM)));

        List<Usuario> caixas = List.of(
                lucasFerreira, alexanderCorreia, danielSousa,
                loganNascimento, sebastianRocha, masonLima,
                ellaFerreira, graysonBarbosa, jackLopes, leviCardoso,
                milaGarcia, asherCosta, leoFonseca, hazelPinto);
        caixas.forEach(u -> u.setPerfis(Set.of(Perfil.CAIXA)));

        // ==================== Empresas ====================
        Empresa emp1 = criarEmpresa("Empresa 1", "Fantasia 1", "1234567890", anaSilva);
        Empresa emp2 = criarEmpresa("Empresa 2", "Fantasia 2", "2345678901", brunoCastro);
        Empresa emp3 = criarEmpresa("Empresa 3", "Fantasia 3", "3456789012", carlaDias);
        Empresa emp4 = criarEmpresa("Empresa 4", "Fantasia 4", "4567890123", zoeFernandes);
        Empresa emp5 = criarEmpresa("Empresa 5", "Fantasia 5", "5678901234", liamSantos);
        Empresa emp6 = criarEmpresa("Empresa 6", "Fantasia 6", "6789012345", oliviaPereira);
        Empresa emp7 = criarEmpresa("Empresa 7", "Fantasia 7", "7890123456", noahRodrigues);
        Empresa emp8 = criarEmpresa("Empresa 8", "Fantasia 8", "8901234567", emmaFerreira);
        Empresa emp9 = criarEmpresa("Empresa 9", "Fantasia 9", "9012345678", isabellaGomes);

        // Vincular usuarios as empresas
        anaSilva.setEmpresa(emp1);
        brunoCastro.setEmpresa(emp2);
        carlaDias.setEmpresa(emp3);
        zoeFernandes.setEmpresa(emp4);
        liamSantos.setEmpresa(emp5);
        oliviaPereira.setEmpresa(emp6);
        noahRodrigues.setEmpresa(emp7);
        emmaFerreira.setEmpresa(emp8);
        isabellaGomes.setEmpresa(emp9);

        // Conta dev MASTER tambem pertence a emp1 (que recebe o seed completo),
        // para conseguir testar as telas operacionais logada como "admin".
        admin.setEmpresa(emp1);

        // ==================== Produtos ====================
        List<Empresa> empresas = List.of(emp1, emp2, emp3, emp4, emp5, emp6, emp7, emp8, emp9);

        // Produtos iniciais da empresa 1
        criarProduto("Produto A",    IMG_HAMBURGUER, 50,  10.5,  25.0, emp1);
        criarProduto("Produto Aaa",  IMG_BEBIDA,     50,  10.5,  25.0, emp1);
        criarProduto("Produto Avvv", IMG_HAMBURGUER, 50,  10.5,  25.0, emp1);
        criarProduto("Produto Accc", IMG_BEBIDA,     50,  10.5,  25.0, emp1);
        criarProduto("Produto F",    IMG_HAMBURGUER, 45,   7.5,  18.0, emp1);
        criarProduto("Produto K",    IMG_HAMBURGUER, 110, 11.0,  28.0, emp1);

        // Produtos compartilhados entre empresas
        criarProduto("Produto B", IMG_HAMBURGUER, 75,  15.0, 35.0, emp2);
        criarProduto("Produto G", IMG_HAMBURGUER, 80,  12.0, 30.0, emp2);
        criarProduto("Produto L", IMG_HAMBURGUER, 70,  13.0, 32.0, emp2);

        criarProduto("Produto C", IMG_HAMBURGUER, 120,  8.0, 20.0, emp3);
        criarProduto("Produto H", IMG_HAMBURGUER, 65,   9.0, 22.0, emp3);
        criarProduto("Produto M", IMG_HAMBURGUER, 40,   6.5, 16.0, emp3);

        criarProduto("Produto D", IMG_HAMBURGUER, 30,   5.5, 12.0, emp4);
        criarProduto("Produto I", IMG_HAMBURGUER, 150, 14.5, 36.0, emp4);
        criarProduto("Produto N", IMG_HAMBURGUER, 55,   8.5, 21.0, emp4);

        criarProduto("Produto E", IMG_HAMBURGUER, 200, 18.0, 45.0, emp5);
        criarProduto("Produto J", IMG_HAMBURGUER, 90,  10.0, 24.0, emp5);
        criarProduto("Produto O", IMG_HAMBURGUER, 85,  16.0, 40.0, emp5);

        // Produtos aleatorios por empresa (20 por empresa, empresas 2-9)
        for (int e = 1; e < empresas.size(); e++) {
            Empresa empresa = empresas.get(e);
            for (int i = 1; i <= 20; i++) {
                criarProduto(
                        "Produto Aleatorio " + (e + 1) + " - " + i,
                        IMG_HAMBURGUER,
                        30 + (i * 5),
                        5.0 + (i * 0.5),
                        12.0 + (i * 1.5),
                        empresa);
            }
        }

        em.flush();

        // ==================== Operacao completa (emp1 e emp2) ====================
        // Cozinha (perfil ausente no seed base) + vinculo de funcionarios as empresas
        Usuario cozinhaEmp1 = criarUsuario("Cozinha Empresa 1", "cozinha1@dominio.com", "cozinha1", "0000000", senhaHash);
        Usuario cozinhaEmp2 = criarUsuario("Cozinha Empresa 2", "cozinha2@dominio.com", "cozinha2", "0000000", senhaHash);
        cozinhaEmp1.setPerfis(Set.of(Perfil.COZINHA));
        cozinhaEmp2.setPerfis(Set.of(Perfil.COZINHA));

        sophiaMartins.setEmpresa(emp1);
        lucasFerreira.setEmpresa(emp1);
        cozinhaEmp1.setEmpresa(emp1);
        avaOliveira.setEmpresa(emp2);
        alexanderCorreia.setEmpresa(emp2);
        cozinhaEmp2.setEmpresa(emp2);
        em.flush();

        popularEmpresa(emp1, sophiaMartins, lucasFerreira, 1);
        popularEmpresa(emp2, avaOliveira, alexanderCorreia, 2);

        em.flush();
        LOG.info("Dados de desenvolvimento inicializados com sucesso. Senha de todos os usuarios: 123456");
    }

    /** Vincula um relacionamento unidirecional (colecao @OneToMany @JoinColumn no lado pai) via UPDATE. */
    private void vincular(String tabela, String coluna, Long fkId, Long rowId) {
        em.createNativeQuery("UPDATE " + tabela + " SET " + coluna + " = :fk WHERE id = :id")
                .setParameter("fk", fkId)
                .setParameter("id", rowId)
                .executeUpdate();
    }

    /**
     * Popula o fluxo operacional completo de uma empresa: tipos, produtos, mesas,
     * caixa aberto (+ movimentos) e fechado, comandas com pedidos em varios status
     * (KDS), pagamento simples, rateado e estornado, cliente + documento fiscal e
     * configuracoes de gateway.
     */
    private void popularEmpresa(Empresa emp, Usuario garcom, Usuario caixaUser, int n) {
        // ---- Tipos de produto ----
        TipoProduto tBebidas = new TipoProduto();
        tBebidas.setNome("Bebidas");
        tBebidas.setCor("#3b82f6");
        em.persist(tBebidas);
        TipoProduto tComidas = new TipoProduto();
        tComidas.setNome("Comidas");
        tComidas.setCor("#ef4444");
        em.persist(tComidas);
        em.flush();
        vincular("tipoproduto", "tipoproduto_empresa", emp.getId(), tBebidas.getId());
        vincular("tipoproduto", "tipoproduto_empresa", emp.getId(), tComidas.getId());

        // ---- Produtos (com tipo) ----
        Produto cerveja = criarProduto("Cerveja", IMG_BEBIDA, 200, 4.0, 10.0, emp);
        Produto refri = criarProduto("Refrigerante", IMG_BEBIDA, 150, 3.0, 8.0, emp);
        Produto burger = criarProduto("Hamburguer", IMG_HAMBURGUER, 80, 12.0, 28.0, emp);
        Produto porcao = criarProduto("Porcao de Batata", IMG_HAMBURGUER, 60, 8.0, 20.0, emp);
        cerveja.setTipoProduto(tBebidas);
        refri.setTipoProduto(tBebidas);
        burger.setTipoProduto(tComidas);
        porcao.setTipoProduto(tComidas);

        // ---- Mesas ----
        Mesa mesa1 = criarMesa("Mesa 1", 4, emp);
        Mesa mesa2 = criarMesa("Mesa 2", 2, emp);
        Mesa balcao = criarMesa("Balcao", 1, emp);

        // ---- Caixa aberto (do caixaUser) + movimentos ----
        Caixa caixa = new Caixa();
        caixa.setNome("Caixa " + caixaUser.getNome());
        caixa.setFechado(false);
        caixa.setValorTotal(0.0);
        caixa.setDataCaixa(LocalDate.now());
        caixa.setUsuario(caixaUser);
        caixa.setValorAbertura(new BigDecimal("100.00"));
        caixa.setHoraAbertura(LocalDateTime.now());
        em.persist(caixa);
        em.flush();
        vincular("caixa", "lista_caixa_empresa", emp.getId(), caixa.getId());
        emp.setCaixaAtual(caixa);
        criarMovimento(TipoMovimentoCaixa.SUPRIMENTO, "50.00", "Reforco de troco", caixa, caixaUser);
        criarMovimento(TipoMovimentoCaixa.SANGRIA, "30.00", "Sangria para o cofre", caixa, caixaUser);

        // ---- Caixa fechado (historico, com diferenca) ----
        Caixa caixaFechado = new Caixa();
        caixaFechado.setNome("Caixa do dia anterior");
        caixaFechado.setFechado(true);
        caixaFechado.setValorTotal(540.0);
        caixaFechado.setDataCaixa(LocalDate.now().minusDays(1));
        caixaFechado.setUsuario(caixaUser);
        caixaFechado.setValorAbertura(new BigDecimal("100.00"));
        caixaFechado.setValorFechamentoEsperado(new BigDecimal("640.00"));
        caixaFechado.setValorFechamentoInformado(new BigDecimal("635.00"));
        caixaFechado.setDiferenca(new BigDecimal("-5.00"));
        caixaFechado.setHoraAbertura(LocalDateTime.now().minusDays(1).withHour(8).withMinute(0));
        caixaFechado.setHoraFechamento(LocalDateTime.now().minusDays(1).withHour(23).withMinute(0));
        caixaFechado.setObservacoesFechamento("Pequena diferenca de troco.");
        caixaFechado.setFechadoPor(caixaUser);
        em.persist(caixaFechado);
        em.flush();
        vincular("caixa", "lista_caixa_empresa", emp.getId(), caixaFechado.getId());

        // ---- Comandas abertas com pedidos em varios status (alimenta o KDS) ----
        Comanda cmd1 = criarComanda("Joao", mesa1, garcom, emp, false);
        criarPedido(cmd1, StatusPedido.AGUARDANDO, new Produto[] { cerveja, burger }, new int[] { 2, 1 });
        criarPedido(cmd1, StatusPedido.PREPARANDO, new Produto[] { porcao }, new int[] { 1 });
        atualizarPrecoComanda(cmd1);

        Comanda cmd2 = criarComanda("Maria", mesa1, garcom, emp, false);
        criarPedido(cmd2, StatusPedido.PRONTO, new Produto[] { refri, burger }, new int[] { 3, 2 });
        atualizarPrecoComanda(cmd2);

        Comanda cmd3 = criarComanda("Mesa 2 - cliente", mesa2, garcom, emp, false);
        criarPedido(cmd3, StatusPedido.AGUARDANDO, new Produto[] { refri }, new int[] { 1 });
        atualizarPrecoComanda(cmd3);

        // ---- Comanda finalizada com pagamento SIMPLES (PIX) ----
        Comanda cmdPaga = criarComanda("Conta fechada (PIX)", balcao, garcom, emp, false);
        criarPedido(cmdPaga, StatusPedido.ENTREGUE, new Produto[] { burger, cerveja }, new int[] { 1, 2 });
        atualizarPrecoComanda(cmdPaga);
        Pagamento pgSimples = criarPagamentoSimples(cmdPaga, caixa, caixaUser, FormaPagamento.PIX);

        // ---- Comanda com pagamento RATEADO por itens ----
        Comanda cmdRateada = criarComanda("Rateado por itens", mesa2, garcom, emp, false);
        Pedido pedRateado = criarPedidoVazio(cmdRateada, StatusPedido.ENTREGUE);
        ItemCompra itBurger = criarItem(pedRateado, burger, 2);
        ItemCompra itRefri = criarItem(pedRateado, refri, 2);
        pedRateado.setValor(itBurger.getPreco() + itRefri.getPreco());
        atualizarPrecoComanda(cmdRateada);
        criarPagamentoRateado(cmdRateada, List.of(itBurger, itRefri), caixa, caixaUser);

        // ---- Comanda com pagamento ESTORNADO (historico) ----
        Comanda cmdEstorno = criarComanda("Pagamento estornado", balcao, garcom, emp, false);
        criarPedido(cmdEstorno, StatusPedido.ENTREGUE, new Produto[] { cerveja }, new int[] { 1 });
        atualizarPrecoComanda(cmdEstorno);
        Pagamento pgEstornado = criarPagamentoSimples(cmdEstorno, caixa, caixaUser, FormaPagamento.CREDITO);
        pgEstornado.setEstornado(true);

        // ---- Cliente + Documento fiscal emulado sobre a comanda paga ----
        Cliente cliente = new Cliente();
        cliente.setNome("Cliente Nota " + n);
        cliente.setCpf("1234567890" + n);
        cliente.setEmail("cliente" + n + "@exemplo.com");
        cliente.setEmpresa(emp);
        em.persist(cliente);

        DocumentoFiscal doc = new DocumentoFiscal();
        doc.setTipo(TipoDocumentoFiscal.NFCE);
        doc.setComanda(cmdPaga);
        doc.setCliente(cliente);
        doc.setEmpresa(emp);
        doc.setNumero("00000" + n);
        doc.setChaveAcesso("3525" + n + "0000000000000000000000000000000000000000");
        doc.setStatusEmissao(StatusDocumentoFiscal.EMITIDO);
        doc.setEmulado(true);
        doc.setEmitidoEm(LocalDateTime.now());
        doc.setUsuarioEmissao(caixaUser);
        doc.setPagamentos(new ArrayList<>(List.of(pgSimples)));
        em.persist(doc);

        // ---- Gateways de pagamento (config) ----
        criarGateway(emp, TipoGateway.ASAAS, AmbienteGateway.SANDBOX, "asaas_sandbox_key_" + n, true);
        criarGateway(emp, TipoGateway.ABACATE_PAY, AmbienteGateway.SANDBOX, "abacate_sandbox_key_" + n, false);

        em.flush();
    }

    private Mesa criarMesa(String identificador, int capacidade, Empresa empresa) {
        Mesa m = new Mesa();
        m.setIdentificador(identificador);
        m.setCapacidade(capacidade);
        m.setEmpresa(empresa);
        em.persist(m);
        return m;
    }

    private Comanda criarComanda(String nome, Mesa mesa, Usuario atendente, Empresa empresa, boolean finalizada) {
        Comanda c = new Comanda();
        c.setNome(nome);
        c.setFinalizada(finalizada);
        c.setTaxaServico(false);
        c.setMesa(mesa);
        c.setAtendente(atendente);
        c.setPreco(0.0);
        em.persist(c);
        em.flush();
        vincular("comanda", "lista_comanda_empresa", empresa.getId(), c.getId());
        return c;
    }

    private Pedido criarPedidoVazio(Comanda comanda, StatusPedido status) {
        Pedido p = new Pedido();
        p.setStatusPedido(status);
        p.setObservacao("");
        p.setQuantidadePessoas(1);
        p.setValor(0.0);
        p.setComanda(comanda);
        em.persist(p);
        em.flush();
        vincular("pedido", "lista_pedido_comanda", comanda.getId(), p.getId());
        return p;
    }

    private Pedido criarPedido(Comanda comanda, StatusPedido status, Produto[] produtos, int[] quantidades) {
        Pedido p = criarPedidoVazio(comanda, status);
        double total = 0.0;
        for (int i = 0; i < produtos.length; i++) {
            ItemCompra item = criarItem(p, produtos[i], quantidades[i]);
            total += item.getPreco();
        }
        p.setValor(total);
        return p;
    }

    private ItemCompra criarItem(Pedido pedido, Produto produto, int quantidade) {
        ItemCompra ic = new ItemCompra();
        ic.setProduto(produto);
        ic.setQuantidade(quantidade);
        ic.setPreco(produto.getValorVenda() * quantidade);
        ic.setPedido(pedido);
        em.persist(ic);
        em.flush();
        vincular("itemcompra", "lista_itemcompra_pedido", pedido.getId(), ic.getId());
        return ic;
    }

    private void atualizarPrecoComanda(Comanda comanda) {
        Double total = em.createQuery(
                "SELECT SUM(p.valor) FROM Pedido p WHERE p.comanda = :c", Double.class)
                .setParameter("c", comanda)
                .getSingleResult();
        comanda.setPreco(total == null ? 0.0 : total);
    }

    private void criarMovimento(TipoMovimentoCaixa tipo, String valor, String motivo, Caixa caixa, Usuario usuario) {
        MovimentoCaixa mv = new MovimentoCaixa();
        mv.setTipo(tipo);
        mv.setValor(new BigDecimal(valor));
        mv.setMotivo(motivo);
        mv.setCaixa(caixa);
        mv.setUsuario(usuario);
        em.persist(mv);
    }

    private Pagamento criarPagamentoSimples(Comanda comanda, Caixa caixa, Usuario caixaUser, FormaPagamento forma) {
        double valor = comanda.getPreco() == null ? 0.0 : comanda.getPreco();
        Pagamento pg = new Pagamento();
        pg.setComanda(comanda);
        pg.setCaixa(caixa);
        pg.setUsuarioCaixa(caixaUser);
        pg.setModo(ModoPagamento.SIMPLES);
        pg.setFormaPagamento(forma);
        pg.setValorTotal(BigDecimal.valueOf(valor));
        pg.setValorPagamento(valor);
        pg.setValorGorjeta(0.0);
        pg.setPagamentoRealizado(true);
        pg.setEstornado(false);
        em.persist(pg);
        comanda.setFinalizada(true);
        comanda.setPagamento(pg);
        return pg;
    }

    private Pagamento criarPagamentoRateado(Comanda comanda, List<ItemCompra> itens, Caixa caixa, Usuario caixaUser) {
        double total = itens.stream().mapToDouble(ItemCompra::getPreco).sum();
        Pagamento pg = new Pagamento();
        pg.setComanda(comanda);
        pg.setCaixa(caixa);
        pg.setUsuarioCaixa(caixaUser);
        pg.setModo(ModoPagamento.RATEADO);
        pg.setFormaPagamento(FormaPagamento.DEBITO);
        pg.setValorTotal(BigDecimal.valueOf(total));
        pg.setValorPagamento(total);
        pg.setValorGorjeta(0.0);
        pg.setPagamentoRealizado(true);
        pg.setEstornado(false);
        em.persist(pg);
        for (ItemCompra item : itens) {
            PagamentoItem pi = new PagamentoItem();
            pi.setPagamento(pg);
            pi.setItemCompra(item);
            pi.setQuantidade(item.getQuantidade());
            pi.setValorAbatido(BigDecimal.valueOf(item.getPreco()));
            em.persist(pi);
        }
        comanda.setFinalizada(true);
        comanda.setPagamento(pg);
        return pg;
    }

    private void criarGateway(Empresa empresa, TipoGateway tipo, AmbienteGateway ambiente, String apiKey, boolean habilitado) {
        EmpresaGatewayConfig cfg = new EmpresaGatewayConfig();
        cfg.setEmpresa(empresa);
        cfg.setTipo(tipo);
        cfg.setAmbiente(ambiente);
        cfg.setApiKey(apiKey);
        cfg.setApiSecret("secret_" + apiKey);
        cfg.setHabilitado(habilitado);
        em.persist(cfg);
    }

    private Usuario criarUsuario(String nome, String email, String login, String cpf, String senhaHash) {
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setEmail(email);
        u.setLogin(login);
        u.setCpf(cpf);
        u.setSenha(senhaHash);
        u.setAtivo(true);
        em.persist(u);
        return u;
    }

    private Empresa criarEmpresa(String nome, String nomeFantasia, String cnpj, Usuario admin) {
        Empresa e = new Empresa();
        e.setNome(nome);
        e.setNomeFantasia(nomeFantasia);
        e.setCnpj(cnpj);
        e.setAdmin(admin);
        e.setAtivo(true);
        em.persist(e);
        return e;
    }

    private Produto criarProduto(String nome, String linkImage, int estoque, double valorCompra, double valorVenda, Empresa empresa) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setLinkimage(linkImage);
        p.setEstoque(estoque);
        p.setValorCompra(valorCompra);
        p.setValorVenda(valorVenda);
        p.setAtivo(true);
        em.persist(p);
        // setar FK via native query pois o mapeamento e unidirecional pelo lado da Empresa
        em.flush();
        em.createNativeQuery("UPDATE produto SET lista_produto_empresa = :empId WHERE id = :prodId")
                .setParameter("empId", empresa.getId())
                .setParameter("prodId", p.getId())
                .executeUpdate();
        return p;
    }
}
