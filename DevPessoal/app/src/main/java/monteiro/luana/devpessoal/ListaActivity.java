package monteiro.luana.devpessoal;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListaActivity extends AppCompatActivity {

    private ArrayList<Tarefa> listaTemas;
    private ListView listView;
    private AdapterC adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tema);

        listView = findViewById(R.id.list_view_temas);
        listaTemas = new ArrayList<>();

        // Carregar arrays do resource
        String[] titulos = getResources().getStringArray(R.array.titulos);
        String[] descricoes = getResources().getStringArray(R.array.descricoes);
        String[] datas = getResources().getStringArray(R.array.datas);
        int[] prioridades = getResources().getIntArray(R.array.prioridades);

        // Instanciar objetos TemaProjeto
        for (int i = 0; i < titulos.length; i++) {
            Tarefa tema = new Tarefa(titulos[i], descricoes[i], datas[i], prioridades[i]);
            listaTemas.add(tema);
        }

        // Configurar Adapter customizado
        adapter = new AdapterC(this, listaTemas);
        listView.setAdapter(adapter);

        // Clique em item da lista
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Tarefa tema = listaTemas.get(position);
            Toast.makeText(this, "Clicou em: " + tema.getTitulo() +
                    "\n Prioridade: " + tema.getPrioridade(),Toast.LENGTH_SHORT).show();
        });
    }
}
