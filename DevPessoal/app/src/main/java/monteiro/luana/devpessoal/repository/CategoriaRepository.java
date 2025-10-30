package monteiro.luana.devpessoal.repository;

import android.app.Application;

import monteiro.luana.devpessoal.database.AppDatabase;
import monteiro.luana.devpessoal.database.CategoriaDao;
import monteiro.luana.devpessoal.model.Categoria;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Repository para gerenciar operações de Categoria
 * Camada de abstração entre DAO e UI
 */
public class CategoriaRepository {

    private CategoriaDao categoriaDao;

    public CategoriaRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        categoriaDao = database.categoriaDao();
    }

    // Inserir
    public long inserir(Categoria categoria) {
        Callable<Long> insertCallable = () -> categoriaDao.inserir(categoria);
        Future<Long> future = AppDatabase.databaseWriteExecutor.submit(insertCallable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Atualizar
    public void atualizar(Categoria categoria) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoriaDao.atualizar(categoria);
        });
    }

    // Excluir
    public void excluir(Categoria categoria) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            categoriaDao.excluir(categoria);
        });
    }

    // Listar todas
    public List<Categoria> listarTodas() {
        Callable<List<Categoria>> callable = () -> categoriaDao.listarTodas();
        Future<List<Categoria>> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Buscar por ID
    public Categoria buscarPorId(int id) {
        Callable<Categoria> callable = () -> categoriaDao.buscarPorId(id);
        Future<Categoria> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Buscar por nome
    public Categoria buscarPorNome(String nome) {
        Callable<Categoria> callable = () -> categoriaDao.buscarPorNome(nome);
        Future<Categoria> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Contar categorias
    public int contarCategorias() {
        Callable<Integer> callable = () -> categoriaDao.contarCategorias();
        Future<Integer> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }
}