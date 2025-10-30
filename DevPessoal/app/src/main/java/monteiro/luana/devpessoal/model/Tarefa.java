package monteiro.luana.devpessoal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.time.LocalDate;

/**
 * Entidade Tarefa com Room Database
 * Relacionamento com Categoria via chave estrangeira
 */
@Entity(
        tableName = "tarefas",
        foreignKeys = @ForeignKey(
                entity = Categoria.class,
                parentColumns = "categoria_id",
                childColumns = "categoria_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("categoria_id")}
)
public class Tarefa {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tarefa_id")
    private int id;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "categoria_id")
    private int categoriaId; // Chave estrangeira

    @ColumnInfo(name = "data_criacao")
    private LocalDate dataCriacao; // Usando LocalDate

    @ColumnInfo(name = "data_conclusao")
    private LocalDate dataConclusao; // Data para conclusão do objetivo

    @ColumnInfo(name = "prioridade")
    private int prioridade; // 1 = Alta, 0 = Baixa

    @ColumnInfo(name = "notificacao")
    private boolean notificacao;

    @ColumnInfo(name = "concluida")
    private boolean concluida;

    // Construtores
    public Tarefa() {
    }

    public Tarefa(String titulo, int categoriaId, LocalDate dataCriacao,
                  LocalDate dataConclusao, int prioridade, boolean notificacao) {
        this.titulo = titulo;
        this.categoriaId = categoriaId;
        this.dataCriacao = dataCriacao;
        this.dataConclusao = dataConclusao;
        this.prioridade = prioridade;
        this.notificacao = notificacao;
        this.concluida = false;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDate dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public boolean isNotificacao() {
        return notificacao;
    }

    public void setNotificacao(boolean notificacao) {
        this.notificacao = notificacao;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }
}