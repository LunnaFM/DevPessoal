package monteiro.luana.devpessoal.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import monteiro.luana.devpessoal.model.Tarefa;
import monteiro.luana.devpessoal.model.TarefaComCategoria;

import java.util.List;

/**
 * DAO (Data Access Object) para a entidade Tarefa
 * Define operações de banco de dados
 */
@Dao
public interface TarefaDao {

    @Insert
    long inserir(Tarefa tarefa);

    @Update
    void atualizar(Tarefa tarefa);

    @Delete
    void excluir(Tarefa tarefa);

    @Query("SELECT * FROM tarefas ORDER BY titulo ASC")
    List<Tarefa> listarTodas();

    @Query("SELECT * FROM tarefas ORDER BY prioridade DESC, titulo ASC")
    List<Tarefa> listarPorPrioridade();

    @Query("SELECT * FROM tarefas ORDER BY data_criacao DESC")
    List<Tarefa> listarPorData();

    @Query("SELECT * FROM tarefas WHERE tarefa_id = :id")
    Tarefa buscarPorId(int id);

    @Query("SELECT * FROM tarefas WHERE categoria_id = :categoriaId")
    List<Tarefa> buscarPorCategoria(int categoriaId);

    @Query("SELECT * FROM tarefas WHERE concluida = :concluida")
    List<Tarefa> buscarPorStatus(boolean concluida);

    @Transaction
    @Query("SELECT * FROM tarefas")
    List<TarefaComCategoria> listarTarefasComCategoria();

    @Transaction
    @Query("SELECT * FROM tarefas ORDER BY titulo ASC")
    List<TarefaComCategoria> listarTarefasComCategoriaPorNome();

    @Transaction
    @Query("SELECT * FROM tarefas ORDER BY prioridade DESC")
    List<TarefaComCategoria> listarTarefasComCategoriaPorPrioridade();

    @Transaction
    @Query("SELECT * FROM tarefas ORDER BY data_criacao DESC")
    List<TarefaComCategoria> listarTarefasComCategoriaPorData();

    @Query("SELECT COUNT(*) FROM tarefas")
    int contarTarefas();

    @Query("DELETE FROM tarefas")
    void excluirTodas();
}