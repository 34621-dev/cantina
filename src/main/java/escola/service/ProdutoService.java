package escola.service;

import java.util.List;

import org.springframework.stereotype.Service;

import escola.exception.ProdutoNaoEncontradoException;
import escola.exception.RegraDeNegocioException;
import escola.model.Produto;
import escola.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Produto buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    public Produto cadastrar(Produto produto) {
        if (produto.getCodigo() == null || produto.getCodigo().trim().isEmpty()) {
            throw new RegraDeNegocioException("Código é obrigatório");
        }
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new RegraDeNegocioException("Nome é obrigatório");
        }
        if (produto.getPreco() == null || produto.getPreco() <= 0) {
            throw new RegraDeNegocioException("O preço precisa ser maior que zero");
        }
        if (produto.getEstoque() < 0) {
            throw new RegraDeNegocioException("O estoque não pode ser negativo");
        }
        if (repository.existsByCodigo(produto.getCodigo())) {
            throw new RegraDeNegocioException("Já existe um produto com o código informado: " + produto.getCodigo());
        }
        return repository.save(produto);
    }

    public Produto vender(Long id) {
        Produto produto = buscar(id);
        if (produto.getEstoque() <= 0) {
            throw new RegraDeNegocioException("Produto sem estoque");
        }
        produto.setEstoque(produto.getEstoque() - 1);
        return repository.save(produto);
    }

    public void excluir(Long id) {
        Produto produto = buscar(id);
        if (produto.getEstoque() > 0) {
            throw new RegraDeNegocioException("Produto com estoque nao pode ser excluido");
        }
        repository.delete(produto);
    }
}