package monteiro.luana.devpessoal.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import monteiro.luana.devpessoal.model.Categoria;
import monteiro.luana.devpessoal.model.Tarefa;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Database principal do aplicativo usando Room
 * Singleton pattern para garantir uma única instância
 */
@Database(entities = {Categoria.class, Tarefa.class}, version = 1, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoriaDao categoriaDao();
    public abstract TarefaDao tarefaDao();

    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;

    // ExecutorService para operações assíncronas
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    /**
     * Retorna a instância única do banco de dados
     */
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "devpessoal_database")
                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Callback para popular o banco com dados iniciais
     */
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Popular banco com categorias padrão
            databaseWriteExecutor.execute(() -> {
                CategoriaDao categoriaDao = INSTANCE.categoriaDao();

                // Limpar dados existentes
                categoriaDao.excluirTodas();

                // Inserir categorias padrão
                categoriaDao.inserir(new Categoria("Pessoal", "Objetivos pessoais e de desenvolvimento individual", "#4CAF50"));
                categoriaDao.inserir(new Categoria("Profissional", "Objetivos relacionados à carreira e trabalho", "#2196F3"));
                categoriaDao.inserir(new Categoria("Acadêmico", "Objetivos de estudos e aprendizado", "#FF9800"));
                categoriaDao.inserir(new Categoria("Saúde", "Objetivos de saúde física e mental", "#F44336"));
                categoriaDao.inserir(new Categoria("Financeiro", "Objetivos financeiros e investimentos", "#4CAF50"));
                categoriaDao.inserir(new Categoria("Lazer", "Objetivos de entretenimento e diversão", "#9C27B0"));
            });
        }
    };
}