package monteiro.luana.devpessoal;

public class Tarefa {
    private String titulo;
    private String descricao;
    private String dataCriacao;
    private int prioridade;
    public Tarefa(String titulo, String descricao, String dataCriacao, int prioridade) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.prioridade = prioridade;
    }

    // Getters e setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }
    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

}
