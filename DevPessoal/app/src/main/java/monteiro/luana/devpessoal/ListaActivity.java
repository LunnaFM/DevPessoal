package monteiro.luana.devpessoal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ListaActivity extends AppCompatActivity {

    private ArrayList<Tarefa> listaTemas;
    private ListView listView;
    private AdapterC adapter;
    private SharedPreferences preferences;

    private static final int REQUEST_CADASTRO = 1;
    private static final int REQUEST_EDITAR = 2;
    private static final String TAG = "ListaActivity";
    private static final String PREF_NAME = "DevPessoalPreferences";

    private ActionMode actionMode;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.aplicarPreferencias(this);
        setContentView(R.layout.activity_lista_tema);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        }

        listView = findViewById(R.id.list_view_temas);
        listaTemas = new ArrayList<>();

        // Não carregar mais dados iniciais - apenas dados cadastrados pelo usuário
        ordenarLista();

        adapter = new AdapterC(this, listaTemas);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Tarefa tema = listaTemas.get(position);
            Toast.makeText(this, getString(R.string.action_list) + ": " + tema.getTitulo() +
                            "\n" + getString(R.string.definir_priodidade) + " " +
                            (tema.getPrioridade() == 1 ? getString(R.string.alta) : getString(R.string.baixa)),
                    Toast.LENGTH_SHORT).show();
        });

        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (actionMode != null) {
                return false;
            }

            selectedPosition = position;
            listView.setItemChecked(position, true);
            actionMode = startActionMode(actionModeCallback);
            view.setSelected(true);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ordenarLista();
        adapter.notifyDataSetChanged();
    }

    private void ordenarLista() {
        String ordenacao = preferences.getString("ordenacao", "nome");

        switch (ordenacao) {
            case "nome":
                Collections.sort(listaTemas, new Comparator<Tarefa>() {
                    @Override
                    public int compare(Tarefa t1, Tarefa t2) {
                        return t1.getTitulo().compareToIgnoreCase(t2.getTitulo());
                    }
                });
                break;
            case "prioridade":
                Collections.sort(listaTemas, new Comparator<Tarefa>() {
                    @Override
                    public int compare(Tarefa t1, Tarefa t2) {
                        return Integer.compare(t2.getPrioridade(), t1.getPrioridade());
                    }
                });
                break;
            case "data":
                Collections.sort(listaTemas, new Comparator<Tarefa>() {
                    @Override
                    public int compare(Tarefa t1, Tarefa t2) {
                        return t2.getDataCriacao().compareTo(t1.getDataCriacao());
                    }
                });
                break;
        }
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_contextual, menu);
            mode.setTitle(getString(R.string.action_list));
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.action_editar) {
                editarItem();
                mode.finish();
                return true;
            } else if (id == R.id.action_excluir) {
                excluirItem();
                mode.finish();
                return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            selectedPosition = -1;
            listView.clearChoices();
            listView.requestLayout();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            abrirCadastro();
            return true;
        } else if (id == R.id.action_author) {
            abrirAutor();
            return true;
        } else if (id == R.id.action_settings) {
            abrirConfiguracoes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirCadastro() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, REQUEST_CADASTRO);
    }

    private void abrirAutor() {
        Intent intent = new Intent(this, AuthorActivity.class);
        startActivity(intent);
    }

    private void abrirConfiguracoes() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void editarItem() {
        if (selectedPosition >= 0 && selectedPosition < listaTemas.size()) {
            Tarefa tarefaSelecionada = listaTemas.get(selectedPosition);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("modo_edicao", true);
            intent.putExtra("posicao_item", selectedPosition);
            intent.putExtra("tarefa_titulo", tarefaSelecionada.getTitulo());
            intent.putExtra("tarefa_descricao", tarefaSelecionada.getDescricao());
            intent.putExtra("tarefa_data", tarefaSelecionada.getDataCriacao());
            intent.putExtra("tarefa_prioridade", tarefaSelecionada.getPrioridade());
            intent.putExtra("tarefa_notificacao", tarefaSelecionada.isNotificacao());

            startActivityForResult(intent, REQUEST_EDITAR);
        }
    }

    private void excluirItem() {
        if (selectedPosition >= 0 && selectedPosition < listaTemas.size()) {
            Tarefa tarefaRemovida = listaTemas.remove(selectedPosition);
            adapter.notifyDataSetChanged();

            Toast.makeText(this, getString(R.string.item_removido) + " " + tarefaRemovida.getTitulo(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            String titulo = data.getStringExtra("tarefa_titulo");
            String descricao = data.getStringExtra("tarefa_descricao");
            String dataCriacao = data.getStringExtra("tarefa_data");
            int prioridade = data.getIntExtra("tarefa_prioridade", 0);
            boolean notificacao = data.getBooleanExtra("tarefa_notificacao", false);

            if (requestCode == REQUEST_CADASTRO) {
                Tarefa novaTarefa = new Tarefa(titulo, descricao, dataCriacao, prioridade, notificacao);
                listaTemas.add(novaTarefa);
                ordenarLista();
                adapter.notifyDataSetChanged();

                Toast.makeText(this, getString(R.string.nova_tarefa_adicionada) + " " + titulo,
                        Toast.LENGTH_SHORT).show();

            } else if (requestCode == REQUEST_EDITAR) {
                int posicao = data.getIntExtra("posicao_item", -1);

                if (posicao >= 0 && posicao < listaTemas.size()) {
                    Tarefa tarefaAtualizada = listaTemas.get(posicao);
                    tarefaAtualizada.setTitulo(titulo);
                    tarefaAtualizada.setDescricao(descricao);
                    tarefaAtualizada.setDataCriacao(dataCriacao);
                    tarefaAtualizada.setPrioridade(prioridade);
                    tarefaAtualizada.setNotificacao(notificacao);

                    ordenarLista();
                    adapter.notifyDataSetChanged();

                    Toast.makeText(this, getString(R.string.tarefa_atualizada) + " " + titulo,
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}