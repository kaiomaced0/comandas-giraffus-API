package k;

import java.util.HashSet;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import io.quarkus.test.junit.QuarkusTest;
import k.model.Empresa;
import k.model.Mesa;
import k.model.Perfil;
import k.model.Produto;
import k.model.TipoProduto;
import k.model.Usuario;
import k.repository.EmpresaRepository;
import k.repository.MesaRepository;
import k.repository.ProdutoRepository;
import k.repository.TipoProdutoRepository;
import k.repository.UsuarioRepository;
import k.service.UsuarioLogadoService;
import org.junit.jupiter.api.BeforeEach;

@QuarkusTest
public abstract class AbstractServiceTest {

    @Inject
    protected EmpresaRepository empresaRepository;

    @Inject
    protected UsuarioRepository usuarioRepository;

    @Inject
    protected ProdutoRepository produtoRepository;

    @Inject
    protected TipoProdutoRepository tipoProdutoRepository;

    @Inject
    protected MesaRepository mesaRepository;

    @Inject
    protected UsuarioLogadoService usuarioLogadoService;

    protected Empresa empresa;
    protected Usuario admin;
    protected Usuario caixa;
    protected TipoProduto tipoProduto;
    protected Produto produto;
    protected Mesa mesa;

    @BeforeEach
    @Transactional
    public void seed() {
        cleanAll();
        empresa = new Empresa();
        empresa.setNome("Empresa Teste");
        empresa.setCnpj("00.000.000/0001-00");
        empresa.setNomeFantasia("Teste");
        empresaRepository.persist(empresa);

        admin = novoUsuario("admin_test", Perfil.ADMIN, empresa);
        caixa = novoUsuario("caixa_test", Perfil.CAIXA, empresa);
        empresa.setAdmin(admin);

        tipoProduto = new TipoProduto();
        tipoProduto.setNome("Bebidas");
        tipoProduto.setCor("#00AAFF");
        tipoProdutoRepository.persist(tipoProduto);
        if (empresa.getTipoProdutos() == null) {
            empresa.setTipoProdutos(new java.util.ArrayList<>());
        }
        empresa.getTipoProdutos().add(tipoProduto);

        produto = new Produto();
        produto.setNome("Cerveja");
        produto.setEstoque(100);
        produto.setValorCompra(5.0);
        produto.setValorVenda(10.0);
        produto.setTipoProduto(tipoProduto);
        produtoRepository.persist(produto);
        if (empresa.getProdutos() == null) {
            empresa.setProdutos(new java.util.ArrayList<>());
        }
        empresa.getProdutos().add(produto);

        mesa = new Mesa();
        mesa.setEmpresa(empresa);
        mesa.setIdentificador("01");
        mesa.setCapacidade(4);
        mesaRepository.persist(mesa);

        if (empresa.getCaixas() == null) {
            empresa.setCaixas(new java.util.ArrayList<>());
        }
        if (empresa.getComandas() == null) {
            empresa.setComandas(new java.util.ArrayList<>());
        }

        TestUsuarioLogadoService.atual = admin;
    }

    @Transactional
    protected void cleanAll() {
        // H2 drop-and-create recria tudo, mas clear in case of multi-test reuse
        TestUsuarioLogadoService.atual = null;
    }

    protected Usuario novoUsuario(String login, Perfil perfil, Empresa empresa) {
        Usuario u = new Usuario();
        u.setLogin(login);
        u.setNome("Nome " + login);
        u.setSenha("hash");
        u.setCpf("00000000000");
        u.setEmail(login + "@test.com");
        u.setEmpresa(empresa);
        Set<Perfil> perfis = new HashSet<>();
        perfis.add(perfil);
        u.setPerfis(perfis);
        usuarioRepository.persist(u);
        return u;
    }

    protected void loginComo(Usuario u) {
        TestUsuarioLogadoService.atual = u;
    }
}
