package monteiro.luana.devpessoal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private EditText etObjectiveName;
    private RadioGroup rgPriority;
    private CheckBox cbNotify;
    private Spinner spinnerCategory;
    private SharedPreferences preferences;

    private boolean modoEdicao = false;
    private int posicaoItem = -1;
    private boolean notificacaoAtiva = false; // Salvar estado do checkbox

    private static final String PREF_NAME = "DevPessoalPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.aplicarPreferencias(this);
        setContentView(R.layout.activity_main);

        // Inicializar SharedPreferences
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Inicializar views
        etObjectiveName = findViewById(R.id.etObjectiveName);
        rgPriority = findViewById(R.id.rgPriority);
        cbNotify = findViewById(R.id.cbNotify);
        spinnerCategory = findViewById(R.id.spinnerCategory);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Aplicar sugestões se habilitado
        aplicarSugestoes();

        // Verificar se está em modo de edição
        verificarModoEdicao();
    }

    private void aplicarSugestoes() {
        boolean sugestoesHabilitadas = preferences.getBoolean(SettingsActivity.KEY_SUGESTOES, false);

        if (sugestoesHabilitadas) {
            etObjectiveName.setHint(R.string.nome_do_objetivo);
            // Aqui você pode adicionar mais sugestões de autocompletar se desejar
        }
    }

    private void verificarModoEdicao() {
        Intent intent = getIntent();
        modoEdicao = intent.getBooleanExtra("modo_edicao", false);

        if (modoEdicao) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.cadastro_de_objetivo) + " - " + getString(R.string.action_editar));
            }

            posicaoItem = intent.getIntExtra("posicao_item", -1);
            String titulo = intent.getStringExtra("tarefa_titulo");
            String descricao = intent.getStringExtra("tarefa_descricao");
            int prioridade = intent.getIntExtra("tarefa_prioridade", 0);
            notificacaoAtiva = intent.getBooleanExtra("tarefa_notificacao", false); // Recuperar checkbox

            preencherCampos(titulo, descricao, prioridade);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.cadastro_de_objetivo);
            }
        }
    }

    private void preencherCampos(String titulo, String descricao, int prioridade) {
        etObjectiveName.setText(titulo);

        if (prioridade == 1) {
            rgPriority.check(R.id.rbHigh);
        } else {
            rgPriority.check(R.id.rbLow);
        }

        // Preencher CheckBox com valor anterior
        cbNotify.setChecked(notificacaoAtiva);

        String[] categorias = getResources().getStringArray(R.array.categories_array);
        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equals(descricao)) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else if (id == R.id.action_salvar) {
            salvarFormulario();
            return true;
        } else if (id == R.id.action_limpar) {
            limparFormulario();
            return true;
        } else if (id == R.id.action_author) {
            Intent intent = new Intent(this, AuthorActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(RESULT_CANCELED);
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    private void limparFormulario() {
        etObjectiveName.setText("");
        rgPriority.clearCheck();
        cbNotify.setChecked(false);
        spinnerCategory.setSelection(0);
        Toast.makeText(this, R.string.valores_limpos, Toast.LENGTH_SHORT).show();
    }

    private void salvarFormulario() {
        String objetivo = etObjectiveName.getText().toString().trim();
        int selectedPriorityId = rgPriority.getCheckedRadioButtonId();

        if (objetivo.isEmpty()) {
            Toast.makeText(this, R.string.preencha_nome, Toast.LENGTH_SHORT).show();
            etObjectiveName.requestFocus();
            return;
        }

        if (selectedPriorityId == -1) {
            Toast.makeText(this, R.string.selecione_prioridade, Toast.LENGTH_SHORT).show();
            rgPriority.requestFocus();
            return;
        }

        RadioButton selectedPriority = findViewById(selectedPriorityId);
        String prioridade = selectedPriority.getText().toString();
        boolean notificar = cbNotify.isChecked();
        String categoria = spinnerCategory.getSelectedItem().toString();

        Tarefa tarefaSalva = new Tarefa(objetivo, categoria,
                java.text.DateFormat.getDateInstance().format(new java.util.Date()),
                prioridade.equals(getString(R.string.alta)) ? 1 : 0,
                notificar);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("tarefa_titulo", tarefaSalva.getTitulo());
        resultIntent.putExtra("tarefa_descricao", tarefaSalva.getDescricao());
        resultIntent.putExtra("tarefa_data", tarefaSalva.getDataCriacao());
        resultIntent.putExtra("tarefa_prioridade", tarefaSalva.getPrioridade());
        resultIntent.putExtra("tarefa_notificacao", tarefaSalva.isNotificacao());

        if (modoEdicao) {
            resultIntent.putExtra("posicao_item", posicaoItem);
        }

        setResult(RESULT_OK, resultIntent);

        int mensagemId = modoEdicao ? R.string.objetivo_atualizado : R.string.objetivo_salvo;
        Toast.makeText(this, mensagemId, Toast.LENGTH_SHORT).show();

        finish();
    }
}