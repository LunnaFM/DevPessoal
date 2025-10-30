package monteiro.luana.devpessoal.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import monteiro.luana.devpessoal.model.Categoria;

import java.util.List;

/**
 * DAO (Data Access Object) para a entidade Categoria
 * Define operações de banco de dados
 */
@Dao
public interface CategoriaDao {

    @Insert
    long inserir(Categoria categoria);

    @Update
    void atualizar(Categoria categoria);

    @Delete
    void excluir(Categoria categoria);

    @Query("SELECT * FROM categorias ORDER BY nome ASC")
    List<Categoria> listarTodas();

    @Query("SELECT * FROM categorias WHERE categoria_id = :id")
    Categoria buscarPorId(int id);

    @Query("SELECT * FROM categorias WHERE nome = :nome")
    Categoria buscarPorNome(String nome);

    @Query("SELECT COUNT(*) FROM categorias")
    int contarCategorias();

    @Query("DELETE FROM categorias")
    void excluirTodas();
}