package monteiro.luana.devpessoal.repository;

import android.app.Application;

import monteiro.luana.devpessoal.database.AppDatabase;
import monteiro.luana.devpessoal.database.TarefaDao;
import monteiro.luana.devpessoal.model.Tarefa;
import monteiro.luana.devpessoal.model.TarefaComCategoria;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Repository para gerenciar operações de Tarefa
 * Camada de abstração entre DAO e UI
 */
public class TarefaRepository {

    private TarefaDao tarefaDao;

    public TarefaRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        tarefaDao = database.tarefaDao();
    }

    // Inserir
    public long inserir(Tarefa tarefa) {
        Callable<Long> insertCallable = () -> tarefaDao.inserir(tarefa);
        Future<Long> future = AppDatabase.databaseWriteExecutor.submit(insertCallable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Atualizar
    public void atualizar(Tarefa tarefa) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            tarefaDao.atualizar(tarefa);
        });
    }

    // Excluir
    public void excluir(Tarefa tarefa) {
        android.util.Log.d("TarefaRepository", "Excluindo tarefa - ID: " + tarefa.getId() + ", Título: " + tarefa.getTitulo());

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                tarefaDao.excluir(tarefa);
                android.util.Log.d("TarefaRepository", "Tarefa excluída com sucesso");
            } catch (Exception e) {
                android.util.Log.e("TarefaRepository", "Erro ao excluir tarefa: " + e.getMessage(), e);
            }
        });
    }

    // Listar todas (nome)
    public List<Tarefa> listarTodas() {
        Callable<List<Tarefa>> callable = () -> tarefaDao.listarTodas();
        Future<List<Tarefa>> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar por prioridade
    public List<Tarefa> listarPorPrioridade() {
        Callable<List<Tarefa>> callable = () -> tarefaDao.listarPorPrioridade();
        Future<List<Tarefa>> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar por data
    public List<Tarefa> listarPorData() {
        Callable<List<Tarefa>> callable = () -> tarefaDao.listarPorData();
        Future<List<Tarefa>> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Listar tarefas com categoria
    public List<TarefaComCategoria> listarTarefasComCategoria(String ordenacao) {
        Callable<List<TarefaComCategoria>> callable;

        switch (ordenacao) {
            case "prioridade":
                callable = () -> tarefaDao.listarTarefasComCategoriaPorPrioridade();
                break;
            case "data":
                callable = () -> tarefaDao.listarTarefasComCategoriaPorData();
                break;
            default:
                callable = () -> tarefaDao.listarTarefasComCategoriaPorNome();
                break;
        }

        Future<List<TarefaComCategoria>> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Buscar por ID
    public Tarefa buscarPorId(int id) {
        Callable<Tarefa> callable = () -> tarefaDao.buscarPorId(id);
        Future<Tarefa> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Buscar por categoria
    public List<Tarefa> buscarPorCategoria(int categoriaId) {
        Callable<List<Tarefa>> callable = () -> tarefaDao.buscarPorCategoria(categoriaId);
        Future<List<Tarefa>> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Contar tarefas
    public int contarTarefas() {
        Callable<Integer> callable = () -> tarefaDao.contarTarefas();
        Future<Integer> future = AppDatabase.databaseWriteExecutor.submit(callable);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }
}