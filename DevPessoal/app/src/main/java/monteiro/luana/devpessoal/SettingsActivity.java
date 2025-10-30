package monteiro.luana.devpessoal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgOrdenacao, rgModoExibicao;
    private CheckBox cbSugestoes, cbVisualizacaoCompleta, cbPortugues;
    private SharedPreferences preferences;

    // Chaves para SharedPreferences
    private static final String PREF_NAME = "DevPessoalPreferences";
    public static final String KEY_ORDENACAO = "ordenacao";
    public static final String KEY_SUGESTOES = "sugestoes";
    public static final String KEY_MODO_NOTURNO = "modo_noturno";
    public static final String KEY_VISUALIZACAO_COMPLETA = "visualizacao_completa";
    public static final String KEY_IDIOMA = "idioma";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.aplicarPreferencias(this);
        setContentView(R.layout.activity_settings);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.settings_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Inicializar SharedPreferences
        preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Inicializar views
        rgOrdenacao = findViewById(R.id.rgOrdenacao);
        rgModoExibicao = findViewById(R.id.rgModoExibicao);
        cbSugestoes = findViewById(R.id.cbSugestoes);
        cbVisualizacaoCompleta = findViewById(R.id.cbVisualizacaoCompleta);
        cbPortugues = findViewById(R.id.cbPortugues);

        // Carregar preferências salvas
        carregarPreferencias();

        // Configurar listeners
        configurarListeners();
    }

    private void carregarPreferencias() {
        // Carregar ordenação
        String ordenacao = preferences.getString(KEY_ORDENACAO, "nome");
        switch (ordenacao) {
            case "nome":
                rgOrdenacao.check(R.id.rbOrdenarNome);
                break;
            case "prioridade":
                rgOrdenacao.check(R.id.rbOrdenarPrioridade);
                break;
            case "data":
                rgOrdenacao.check(R.id.rbOrdenarData);
                break;
        }

        // Carregar sugestões
        cbSugestoes.setChecked(preferences.getBoolean(KEY_SUGESTOES, false));

        // Carregar modo noturno
        boolean modoNoturno = preferences.getBoolean(KEY_MODO_NOTURNO, false);
        if (modoNoturno) {
            rgModoExibicao.check(R.id.rbModoNoturno);
        } else {
            rgModoExibicao.check(R.id.rbModoNormal);
        }

        // Carregar visualização completa
        cbVisualizacaoCompleta.setChecked(preferences.getBoolean(KEY_VISUALIZACAO_COMPLETA, false));

        // Carregar idioma
        // Padrão: inglês (en) → checkbox desmarcado
        // Opcional: português (pt) → checkbox marcado
        String idioma = preferences.getString(KEY_IDIOMA, "en");
        cbPortugues.setChecked(idioma.equals("pt"));
    }

    private void configurarListeners() {
        // Listener para ordenação
        rgOrdenacao.setOnCheckedChangeListener((group, checkedId) -> {
            salvarOrdenacao(checkedId);
        });

        // Listener para modo de exibição (dia/noite)
        rgModoExibicao.setOnCheckedChangeListener((group, checkedId) -> {
            salvarModoExibicao(checkedId);
        });

        // Listener para sugestões
        cbSugestoes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(KEY_SUGESTOES, isChecked).apply();
            Toast.makeText(this, R.string.configuracoes_salvas, Toast.LENGTH_SHORT).show();
        });

        // Listener para visualização completa
        cbVisualizacaoCompleta.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(KEY_VISUALIZACAO_COMPLETA, isChecked).apply();
            Toast.makeText(this, R.string.configuracoes_salvas, Toast.LENGTH_SHORT).show();
        });

        // Listener para idioma (checkbox)
        cbPortugues.setOnCheckedChangeListener((buttonView, isChecked) -> {
            salvarIdioma(isChecked);
        });
    }

    private void salvarOrdenacao(int checkedId) {
        String ordenacao = "nome";

        if (checkedId == R.id.rbOrdenarNome) {
            ordenacao = "nome";
        } else if (checkedId == R.id.rbOrdenarPrioridade) {
            ordenacao = "prioridade";
        } else if (checkedId == R.id.rbOrdenarData) {
            ordenacao = "data";
        }

        preferences.edit().putString(KEY_ORDENACAO, ordenacao).apply();
        Toast.makeText(this, R.string.configuracoes_salvas, Toast.LENGTH_SHORT).show();
    }

    private void salvarModoExibicao(int checkedId) {
        boolean modoNoturno = (checkedId == R.id.rbModoNoturno);

        preferences.edit().putBoolean(KEY_MODO_NOTURNO, modoNoturno).apply();

        // Aplicar o tema imediatamente
        if (modoNoturno) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        Toast.makeText(this, R.string.configuracoes_salvas, Toast.LENGTH_SHORT).show();
    }

    private void salvarIdioma(boolean usarPortugues) {
        // Checkbox marcado → Português (pt)
        // Checkbox desmarcado → Inglês (en) - padrão
        String idioma = usarPortugues ? "pt" : "en";

        preferences.edit().putString(KEY_IDIOMA, idioma).apply();

        // Aplicar o idioma
        mudarIdioma(idioma);

        Toast.makeText(this, R.string.configuracoes_salvas, Toast.LENGTH_SHORT).show();

        // Recriar TODAS as activities abertas para aplicar idioma
        Intent broadcastIntent = new Intent("monteiro.luana.devpessoal.RECREATE_ACTIVITIES");
        sendBroadcast(broadcastIntent);

        // Reiniciar esta activity
        recreate();
    }

    private void mudarIdioma(String codigoIdioma) {
        Locale locale = new Locale(codigoIdioma);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}