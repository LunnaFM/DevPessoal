package monteiro.luana.devpessoal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import monteiro.luana.devpessoal.adapter.AdapterTarefa;
import monteiro.luana.devpessoal.model.Tarefa;
import monteiro.luana.devpessoal.model.TarefaComCategoria;
import monteiro.luana.devpessoal.repository.TarefaRepository;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ArrayList<TarefaComCategoria> listaTarefas;
    private ListView listView;
    private AdapterTarefa adapter;
    private SharedPreferences preferences;
    private TarefaRepository tarefaRepository;

    private static final int REQUEST_CADASTRO = 1;
    private static final int REQUEST_EDITAR = 2;
    private static final String PREF_NAME = "DevPessoalPreferences";

    private ActionMode actionMode;
    private int selectedPosition = -1;

    // BroadcastReceiver para recriar activity quando idioma mudar
    private BroadcastReceiver recreateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            recreate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.aplicarPreferencias(this);
        setContentView(R.layout.activity_lista_tema);

        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        tarefaRepository = new TarefaRepository(getApplication());

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
        }

        listView = findViewById(R.id.list_view_temas);
        listaTarefas = new ArrayList<>();

        adapter = new AdapterTarefa(this, listaTarefas);
        listView.setAdapter(adapter);

        carregarTarefas();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            TarefaComCategoria item = listaTarefas.get(position);
            Toast.makeText(this, getString(R.string.action_list) + ": " + item.getTarefa().getTitulo() +
                            "\n" + getString(R.string.categoria) + " " + item.getCategoria().getNome(),
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

        // Registrar listener para mudanças nas preferências
        preferences.registerOnSharedPreferenceChangeListener(this);

        // Registrar receiver para recriar activity quando idioma mudar
        IntentFilter filter = new IntentFilter("monteiro.luana.devpessoal.RECREATE_ACTIVITIES");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(recreateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(recreateReceiver, filter);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Atualizar quando visualização completa ou ordenação mudar
        if ("visualizacao_completa".equals(key) || "ordenacao".equals(key)) {
            // Recriar adapter com nova configuração
            adapter = new AdapterTarefa(this, listaTarefas);
            listView.setAdapter(adapter);
            // Recarregar dados
            carregarTarefas();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarTarefas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Desregistrar listener para evitar memory leaks
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        try {
            unregisterReceiver(recreateReceiver);
        } catch (Exception e) {
            // Receiver já desregistrado
        }
    }

    private void carregarTarefas() {
        new Thread(() -> {
            String ordenacao = preferences.getString("ordenacao", "nome");
            List<TarefaComCategoria> tarefas = tarefaRepository.listarTarefasComCategoria(ordenacao);

            runOnUiThread(() -> {
                listaTarefas.clear();
                if (tarefas != null) {
                    listaTarefas.addAll(tarefas);
                }
                adapter.notifyDataSetChanged();
            });
        }).start();
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
                confirmarExclusao();
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
        } else if (id == R.id.action_categorias) {
            abrirCategorias();
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

    private void abrirCategorias() {
        try {
            android.util.Log.d("ListaActivity", "Tentando abrir CategoriaActivity");
            Intent intent = new Intent(this, CategoriaActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            android.util.Log.e("ListaActivity", "ERRO ao abrir categorias: " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao abrir categorias: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void editarItem() {
        if (selectedPosition >= 0 && selectedPosition < listaTarefas.size()) {
            TarefaComCategoria item = listaTarefas.get(selectedPosition);
            Tarefa tarefa = item.getTarefa();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("modo_edicao", true);
            intent.putExtra("tarefa_id", tarefa.getId());

            startActivityForResult(intent, REQUEST_EDITAR);
        }
    }

    private void confirmarExclusao() {
        if (selectedPosition >= 0 && selectedPosition < listaTarefas.size()) {
            TarefaComCategoria item = listaTarefas.get(selectedPosition);
            Tarefa tarefa = item.getTarefa();

            // IMPORTANTE: Salvar a posição e a tarefa ANTES de abrir o diálogo
            // porque o ActionMode vai resetar selectedPosition para -1
            final int posicaoParaExcluir = selectedPosition;
            final Tarefa tarefaParaExcluir = tarefa;
            final String tituloTarefa = tarefa.getTitulo();

            // Log para debug
            android.util.Log.d("ListaActivity", "Confirmando exclusão - Posição: " + posicaoParaExcluir + ", ID: " + tarefaParaExcluir.getId() + ", Título: " + tituloTarefa);

            // AlertDialog para confirmar exclusão
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirmar_exclusao)
                    .setMessage(getString(R.string.mensagem_confirmar_exclusao) + " \"" + tituloTarefa + "\"?")
                    .setPositiveButton(R.string.sim, (dialog, which) -> {
                        android.util.Log.d("ListaActivity", "Usuário confirmou exclusão - Executando...");
                        excluirItem(tarefaParaExcluir, tituloTarefa);
                    })
                    .setNegativeButton(R.string.nao, (dialog, which) -> {
                        android.util.Log.d("ListaActivity", "Usuário cancelou exclusão");
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            android.util.Log.e("ListaActivity", "Posição inválida ao confirmar: " + selectedPosition);
        }
    }

    private void excluirItem(Tarefa tarefa, String tituloTarefa) {
        android.util.Log.d("ListaActivity", "Iniciando exclusão - ID: " + tarefa.getId() + ", Título: " + tituloTarefa);

        new Thread(() -> {
            try {
                tarefaRepository.excluir(tarefa);
                android.util.Log.d("ListaActivity", "Tarefa excluída do banco");

                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.item_removido) + " " + tituloTarefa,
                            Toast.LENGTH_SHORT).show();
                    carregarTarefas();
                });
            } catch (Exception e) {
                android.util.Log.e("ListaActivity", "Erro ao excluir: " + e.getMessage(), e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erro ao excluir: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            carregarTarefas();
        }
    }
}