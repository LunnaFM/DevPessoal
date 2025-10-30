package monteiro.luana.devpessoal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import androidx.appcompat.app.AppCompatDelegate;
import java.util.Locale;

public class PreferenceManager {

    private static final String PREF_NAME = "DevPessoalPreferences";

    /**
     * Aplica todas as preferências salvas (tema, idioma, etc)
     * Deve ser chamado no onCreate de todas as Activities principais
     */
    public static void aplicarPreferencias(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Aplicar tema (modo noturno)
        boolean modoNoturno = prefs.getBoolean("modo_noturno", false);
        if (modoNoturno) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Aplicar idioma - PADRÃO: INGLÊS (en)
        String idioma = prefs.getString("idioma", "en");
        aplicarIdioma(context, idioma);
    }

    /**
     * Aplica o idioma salvo
     */
    private static void aplicarIdioma(Context context, String codigoIdioma) {
        Locale locale = new Locale(codigoIdioma);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }
}