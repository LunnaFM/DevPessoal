package monteiro.luana.devpessoal;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterC extends BaseAdapter {

    private Context context;
    private ArrayList<Tarefa> temas;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "DevPessoalPreferences";

    public AdapterC(Context context, ArrayList<Tarefa> temas) {
        this.context = context;
        this.temas = temas;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return temas.size();
    }

    @Override
    public Object getItem(int position) {
        return temas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Verificar se deve usar visualização completa
        boolean visualizacaoCompleta = preferences.getBoolean("visualizacao_completa", false);

        ViewHolder holder;

        if (convertView == null) {
            if (visualizacaoCompleta) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_lista_tema_completo, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_lista_tema, parent, false);
            }

            holder = new ViewHolder();
            holder.tituloView = convertView.findViewById(R.id.tvTitulo);
            holder.descricaoView = convertView.findViewById(R.id.tvDescricao);

            if (visualizacaoCompleta) {
                holder.prioridadeView = convertView.findViewById(R.id.tvPrioridade);
                holder.dataView = convertView.findViewById(R.id.tvData);
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tarefa tema = temas.get(position);
        holder.tituloView.setText(tema.getTitulo());
        holder.descricaoView.setText(tema.getDescricao());

        // Se visualização completa, preencher prioridade e data
        if (visualizacaoCompleta && holder.prioridadeView != null && holder.dataView != null) {
            // Prioridade
            String prioridade = tema.getPrioridade() == 1 ? "Alta" : "Baixa";
            holder.prioridadeView.setText("Prioridade: " + prioridade);

            // Data formatada (dd/mm/yyyy)
            String dataFormatada = formatarData(tema.getDataCriacao());
            holder.dataView.setText(dataFormatada);
        }

        return convertView;
    }

    private String formatarData(String dataOriginal) {
        try {
            // Tentar diferentes formatos de data
            SimpleDateFormat formatoEntrada;

            // Se já estiver no formato dd/MM/yyyy, retornar direto
            if (dataOriginal.matches("\\d{2}/\\d{2}/\\d{4}")) {
                return dataOriginal;
            }

            // Tentar formato padrão do Java (ex: "Jan 15, 2024" ou similar)
            formatoEntrada = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date data = formatoEntrada.parse(dataOriginal);

            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formatoSaida.format(data);

        } catch (ParseException e) {
            // Se falhar, tentar formato ISO (yyyy-MM-dd)
            try {
                SimpleDateFormat formatoISO = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date data = formatoISO.parse(dataOriginal);

                SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return formatoSaida.format(data);
            } catch (ParseException ex) {
                // Se tudo falhar, retornar data original
                return dataOriginal;
            }
        }
    }

    private static class ViewHolder {
        TextView tituloView;
        TextView descricaoView;
        TextView prioridadeView;
        TextView dataView;
    }
}