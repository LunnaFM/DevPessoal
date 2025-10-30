package monteiro.luana.devpessoal;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import monteiro.luana.devpessoal.adapter.AdapterCategoria;
import monteiro.luana.devpessoal.model.Categoria;
import monteiro.luana.devpessoal.repository.CategoriaRepository;

import java.util.ArrayList;
import java.util.List;

public class CategoriaActivity extends AppCompatActivity {

    private static final String TAG = "CategoriaActivity";
    private ListView listView;
    private AdapterCategoria adapter;
    private ArrayList<Categoria> listaCategorias;
    private CategoriaRepository categoriaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate iniciado");

        try {
            PreferenceManager.aplicarPreferencias(this);
            setContentView(R.layout.activity_categoria);

            categoriaRepository = new CategoriaRepository(getApplication());

            Log.d(TAG, "Repository criado");

            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(R.string.categorias);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                }
                Log.d(TAG, "Toolbar configurada");
            }

            listView = findViewById(R.id.list_view_categorias);
            listaCategorias = new ArrayList<>();

            adapter = new AdapterCategoria(this, listaCategorias);
            listView.setAdapter(adapter);

            Log.d(TAG, "ListView configurada");

            carregarCategorias();

            listView.setOnItemClickListener((parent, view, position, id) -> {
                Categoria categoria = listaCategorias.get(position);
                Toast.makeText(this,
                        categoria.getNome() + "\n" + categoria.getDescricao(),
                        Toast.LENGTH_SHORT).show();
            });

            Log.d(TAG, "onCreate finalizado com sucesso");

        } catch (Exception e) {
            Log.e(TAG, "Erro no onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void carregarCategorias() {
        Log.d(TAG, "Iniciando carregamento de categorias");

        new Thread(() -> {
            try {
                List<Categoria> categorias = categoriaRepository.listarTodas();
                Log.d(TAG, "Categorias carregadas: " + (categorias != null ? categorias.size() : 0));

                runOnUiThread(() -> {
                    listaCategorias.clear();
                    if (categorias != null) {
                        listaCategorias.addAll(categorias);
                    }
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Adapter atualizado");
                });
            } catch (Exception e) {
                Log.e(TAG, "Erro ao carregar categorias: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erro ao carregar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp chamado");
        onBackPressed();
        return true;
    }
}