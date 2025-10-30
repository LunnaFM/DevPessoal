package monteiro.luana.devpessoal.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import monteiro.luana.devpessoal.R;
import monteiro.luana.devpessoal.model.Categoria;

import java.util.ArrayList;

public class AdapterCategoria extends BaseAdapter {

    private Context context;
    private ArrayList<Categoria> categorias;

    public AdapterCategoria(Context context, ArrayList<Categoria> categorias) {
        this.context = context;
        this.categorias = categorias;
    }

    @Override
    public int getCount() {
        return categorias.size();
    }

    @Override
    public Object getItem(int position) {
        return categorias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categorias.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lista_categoria, parent, false);

            holder = new ViewHolder();
            holder.tvNome = convertView.findViewById(R.id.tvNomeCategoria);
            holder.tvDescricao = convertView.findViewById(R.id.tvDescricaoCategoria);
            holder.viewCor = convertView.findViewById(R.id.viewCorCategoria);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Categoria categoria = categorias.get(position);
        holder.tvNome.setText(categoria.getNome());
        holder.tvDescricao.setText(categoria.getDescricao());

        // Definir cor da categoria
        try {
            holder.viewCor.setBackgroundColor(Color.parseColor(categoria.getCor()));
        } catch (Exception e) {
            holder.viewCor.setBackgroundColor(Color.GRAY);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNome;
        TextView tvDescricao;
        View viewCor;
    }
}