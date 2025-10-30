package monteiro.luana.devpessoal.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

/**
 * Entidade Categoria - representa as categorias de objetivos
 */
@Entity(tableName = "categorias")
public class Categoria {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "categoria_id")
    private int id;

    @ColumnInfo(name = "nome")
    private String nome;

    @ColumnInfo(name = "descricao")
    private String descricao;

    @ColumnInfo(name = "cor")
    private String cor; // Cor hexadecimal para representação visual

    // Construtores
    public Categoria() {
    }

    public Categoria(String nome, String descricao, String cor) {
        this.nome = nome;
        this.descricao = descricao;
        this.cor = cor;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    @Override
    public String toString() {
        return nome; // Para exibição no Spinner
    }
}