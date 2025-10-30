package monteiro.luana.devpessoal.model;

import androidx.room.Embedded;
import androidx.room.Relation;

/**
 * Classe para representar Tarefa com sua Categoria relacionada
 * Utiliza @Embedded e @Relation para fazer JOIN automático
 */
public class TarefaComCategoria {

    @Embedded
    public Tarefa tarefa;

    @Relation(
            parentColumn = "categoria_id",
            entityColumn = "categoria_id"
    )
    public Categoria categoria;

    // Getters
    public Tarefa getTarefa() {
        return tarefa;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}