package monteiro.luana.devpessoal.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import monteiro.luana.devpessoal.R;
import monteiro.luana.devpessoal.model.TarefaComCategoria;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class AdapterTarefa extends BaseAdapter {

    private Context context;
    private ArrayList<TarefaComCategoria> tarefas;
    private SharedPreferences preferences;
    private static final String PREF_NAME = "DevPessoalPreferences";

    public AdapterTarefa(Context context, ArrayList<TarefaComCategoria> tarefas) {
        this.context = context;
        this.tarefas = tarefas;
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return tarefas.size();
    }

    @Override
    public Object getItem(int position) {
        return tarefas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tarefas.get(position).getTarefa().getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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

        TarefaComCategoria item = tarefas.get(position);
        holder.tituloView.setText(item.getTarefa().getTitulo());
        holder.descricaoView.setText(item.getCategoria().getNome());

        if (visualizacaoCompleta && holder.prioridadeView != null && holder.dataView != null) {
            // Prioridade
            String prioridade = item.getTarefa().getPrioridade() == 1 ? "Alta" : "Baixa";
            holder.prioridadeView.setText("Prioridade: " + prioridade);

            // Data formatada
            String dataFormatada = formatarData(item.getTarefa().getDataCriacao());
            holder.dataView.setText(dataFormatada);
        }

        return convertView;
    }

    private String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }

        String idioma = preferences.getString("idioma", "pt");
        Locale locale = idioma.equals("en") ? Locale.ENGLISH : new Locale("pt", "BR");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", locale);
        return data.format(formatter);
    }

    private static class ViewHolder {
        TextView tituloView;
        TextView descricaoView;
        TextView prioridadeView;
        TextView dataView;
    }
}