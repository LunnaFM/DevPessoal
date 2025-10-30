package monteiro.luana.devpessoal;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import monteiro.luana.devpessoal.model.Categoria;
import monteiro.luana.devpessoal.model.Tarefa;
import monteiro.luana.devpessoal.repository.CategoriaRepository;
import monteiro.luana.devpessoal.repository.TarefaRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etObjectiveName;
    private RadioGroup rgPriority;
    private CheckBox cbNotify;
    private Spinner spinnerCategory;
    private Button btnDataConclusao;
    private SharedPreferences preferences;

    private TarefaRepository tarefaRepository;
    private CategoriaRepository categoriaRepository;
    private List<Categoria> listaCategorias;

    private boolean modoEdicao = false;
    private int tarefaIdEdicao = -1;
    private LocalDate dataConclusaoSelecionada;

    private static final String PREF_NAME = "DevPessoalPreferences";

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
        setContentView(R.layout.activity_main);

        // Inicializar repositórios
        tarefaRepository = new TarefaRepository(getApplication());
        categoriaRepository = new CategoriaRepository(getApplication());

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
        btnDataConclusao = findViewById(R.id.btnDataConclusao);

        // Carregar categorias no spinner
        carregarCategorias();

        // Configurar DatePicker
        configurarDatePicker();

        // Aplicar sugestões se habilitado
        aplicarSugestoes();

        // Verificar se está em modo de edição
        verificarModoEdicao();

        // Registrar receiver para recriar activity quando idioma mudar
        IntentFilter filter = new IntentFilter("monteiro.luana.devpessoal.RECREATE_ACTIVITIES");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(recreateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(recreateReceiver, filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(recreateReceiver);
        } catch (Exception e) {
            // Receiver já desregistrado
        }
    }

    private void carregarCategorias() {
        new Thread(() -> {
            listaCategorias = categoriaRepository.listarTodas();

            runOnUiThread(() -> {
                if (listaCategorias != null && !listaCategorias.isEmpty()) {
                    ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            listaCategorias
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adapter);
                }
            });
        }).start();
    }

    private void configurarDatePicker() {
        btnDataConclusao.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            // Se já existe data selecionada, usar ela
            if (dataConclusaoSelecionada != null) {
                calendar.set(dataConclusaoSelecionada.getYear(),
                        dataConclusaoSelecionada.getMonthValue() - 1,
                        dataConclusaoSelecionada.getDayOfMonth());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        dataConclusaoSelecionada = LocalDate.of(year, month + 1, dayOfMonth);
                        atualizarTextoBotaoData();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            // Data mínima é hoje
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void atualizarTextoBotaoData() {
        if (dataConclusaoSelecionada != null) {
            String idioma = preferences.getString("idioma", "pt");
            Locale locale = idioma.equals("en") ? Locale.ENGLISH : new Locale("pt", "BR");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", locale);
            btnDataConclusao.setText(dataConclusaoSelecionada.format(formatter));
        }
    }

    private void aplicarSugestoes() {
        boolean sugestoesHabilitadas = preferences.getBoolean(SettingsActivity.KEY_SUGESTOES, false);

        if (sugestoesHabilitadas) {
            etObjectiveName.setHint(R.string.nome_do_objetivo);
        }
    }

    private void verificarModoEdicao() {
        Intent intent = getIntent();
        modoEdicao = intent.getBooleanExtra("modo_edicao", false);

        if (modoEdicao) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getString(R.string.cadastro_de_objetivo) + " - " + getString(R.string.action_editar));
            }

            tarefaIdEdicao = intent.getIntExtra("tarefa_id", -1);

            if (tarefaIdEdicao != -1) {
                new Thread(() -> {
                    Tarefa tarefa = tarefaRepository.buscarPorId(tarefaIdEdicao);

                    runOnUiThread(() -> {
                        if (tarefa != null) {
                            preencherCampos(tarefa);
                        }
                    });
                }).start();
            }
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.cadastro_de_objetivo);
            }
        }
    }

    private void preencherCampos(Tarefa tarefa) {
        etObjectiveName.setText(tarefa.getTitulo());

        if (tarefa.getPrioridade() == 1) {
            rgPriority.check(R.id.rbHigh);
        } else {
            rgPriority.check(R.id.rbLow);
        }

        cbNotify.setChecked(tarefa.isNotificacao());

        // Data de conclusão
        dataConclusaoSelecionada = tarefa.getDataConclusao();
        atualizarTextoBotaoData();

        // Selecionar categoria no spinner
        if (listaCategorias != null) {
            for (int i = 0; i < listaCategorias.size(); i++) {
                if (listaCategorias.get(i).getId() == tarefa.getCategoriaId()) {
                    spinnerCategory.setSelection(i);
                    break;
                }
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
        dataConclusaoSelecionada = null;
        btnDataConclusao.setText(R.string.selecionar_data);
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

        if (spinnerCategory.getSelectedItemPosition() == 0 || spinnerCategory.getSelectedItem() == null) {
            Toast.makeText(this, R.string.selecione_categoria, Toast.LENGTH_SHORT).show();
            return;
        }

        if (dataConclusaoSelecionada == null) {
            Toast.makeText(this, R.string.selecione_data_conclusao, Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton selectedPriority = findViewById(selectedPriorityId);
        int prioridade = selectedPriority.getText().toString().equals(getString(R.string.alta)) ? 1 : 0;
        boolean notificar = cbNotify.isChecked();
        Categoria categoriaSelecionada = (Categoria) spinnerCategory.getSelectedItem();

        Tarefa tarefa = new Tarefa(
                objetivo,
                categoriaSelecionada.getId(),
                LocalDate.now(),
                dataConclusaoSelecionada,
                prioridade,
                notificar
        );

        new Thread(() -> {
            if (modoEdicao && tarefaIdEdicao != -1) {
                tarefa.setId(tarefaIdEdicao);
                tarefaRepository.atualizar(tarefa);

                runOnUiThread(() -> {
                    Toast.makeText(this, R.string.objetivo_atualizado, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } else {
                long id = tarefaRepository.inserir(tarefa);

                runOnUiThread(() -> {
                    if (id > 0) {
                        Toast.makeText(this, R.string.objetivo_salvo, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(this, "Erro ao salvar objetivo", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
}