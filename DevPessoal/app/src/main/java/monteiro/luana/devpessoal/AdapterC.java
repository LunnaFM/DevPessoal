package monteiro.luana.devpessoal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterC extends BaseAdapter {

    private Context context;
    private ArrayList<Tarefa> temas;

    public AdapterC(Context context, ArrayList<Tarefa> temas) {
        this.context = context;
        this.temas = temas;
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
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lista_tema, parent, false);
            holder = new ViewHolder();
            holder.tituloView = convertView.findViewById(R.id.tvTitulo);
            holder.descricaoView = convertView.findViewById(R.id.tvDescricao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Tarefa tema = temas.get(position);
        holder.tituloView.setText(tema.getTitulo());
        holder.descricaoView.setText(tema.getDescricao());

        return convertView;
    }

    private static class ViewHolder {
        TextView tituloView;
        TextView descricaoView;
    }
}