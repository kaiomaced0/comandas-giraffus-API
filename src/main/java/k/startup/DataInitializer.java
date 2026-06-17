package k.startup;

import java.util.List;
import java.util.Set;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import k.model.Empresa;
import k.model.Perfil;
import k.model.Produto;
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
        LOG.info("Dados de desenvolvimento inicializados com sucesso. Senha de todos os usuarios: 123456");
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
