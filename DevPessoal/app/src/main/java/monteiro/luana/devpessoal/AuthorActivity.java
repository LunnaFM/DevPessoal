package monteiro.luana.devpessoal;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AuthorActivity extends AppCompatActivity {

    private TextView tvAuthorName, tvAuthorCourse, tvAuthorEmail, tvAppDescription, tvUniversityName;
    private ImageView ivAuthorLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.aplicarPreferencias(this);
        setContentView(R.layout.activity_author);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Configurar botão Up (voltar)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sobre o Autor");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Inicializar views
        tvAuthorName = findViewById(R.id.tvAuthorName);
        tvAuthorCourse = findViewById(R.id.tvAuthorCourse);
        tvAuthorEmail = findViewById(R.id.tvAuthorEmail);
        tvAppDescription = findViewById(R.id.tvAppDescription);
        tvUniversityName = findViewById(R.id.tvUniversityName);
        ivAuthorLogo = findViewById(R.id.ivAuthorLogo);

        // Definir dados do autor - SUBSTITUA PELOS SEUS DADOS
        tvAuthorName.setText("Luana Monteiro");
        tvAuthorCourse.setText("Engenharia de Software"); // ou seu curso
        tvAuthorEmail.setText("luana.monteiro@alunos.utfpr.edu.br"); // seu email
        tvAppDescription.setText("Este aplicativo foi desenvolvido como parte da disciplina de " +
                "Desenvolvimento Mobile. Permite o cadastro e listagem de objetivos pessoais " +
                "com diferentes categorias e prioridades.");
        tvUniversityName.setText("UTFPR - Universidade Tecnológica Federal do Paraná");

        // Definir logo da UTFPR
        ivAuthorLogo.setImageResource(R.drawable.utfpr_logo); // substitua pelo nome exato do arquivo
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Botão voltar pressionado
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        // Voltar para a tela anterior
        super.onBackPressed();
    }
}