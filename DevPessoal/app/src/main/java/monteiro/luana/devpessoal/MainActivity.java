package monteiro.luana.devpessoal;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etObjectiveName;
    private RadioGroup rgPriority;
    private CheckBox cbNotify;
    private Spinner spinnerCategory;
    private Button btnClear, btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etObjectiveName = findViewById(R.id.etObjectiveName);
        rgPriority = findViewById(R.id.rgPriority);
        cbNotify = findViewById(R.id.cbNotify);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnClear = findViewById(R.id.btnLimpa);
        btnSave = findViewById(R.id.btnSalva);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Botão Limpar
        btnClear.setOnClickListener(v -> limparFormulario());

        // Botão Salvar
        btnSave.setOnClickListener(v -> salvarFormulario());

    }

    private void limparFormulario() {
        etObjectiveName.setText("");
        rgPriority.clearCheck();
        cbNotify.setChecked(false);
        spinnerCategory.setSelection(0);
        Toast.makeText(this, "Formulário limpo", Toast.LENGTH_SHORT).show();
    }

    private void salvarFormulario() {
        String objetivo = etObjectiveName.getText().toString().trim();
        int selectedPriorityId = rgPriority.getCheckedRadioButtonId();

        if (objetivo.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha o nome do objetivo.", Toast.LENGTH_SHORT).show();
            etObjectiveName.requestFocus();
            return;
        }

        if (selectedPriorityId == -1) {
            Toast.makeText(this, "Por favor, selecione uma prioridade.", Toast.LENGTH_SHORT).show();
            rgPriority.requestFocus();
            return;
        }

        RadioButton selectedPriority = findViewById(selectedPriorityId);
        String prioridade = selectedPriority.getText().toString();
        boolean notificar = cbNotify.isChecked();
        String categoria = spinnerCategory.getSelectedItem().toString();

        Toast.makeText(this, "Dados salvos:\nObjetivo: " + objetivo +
                "\nCategoria: " + categoria, Toast.LENGTH_LONG).show();
    }
}