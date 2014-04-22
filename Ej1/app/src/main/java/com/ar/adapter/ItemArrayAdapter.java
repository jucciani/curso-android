package com.ar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ar.R;
import com.ar.dto.Item;

import java.util.ArrayList;

/**
 * Created by jucciani on 21/04/14.
 */
public class ItemArrayAdapter extends ArrayAdapter<Item> {

    public ItemArrayAdapter(Context context, ArrayList<Item> itemsList){
        super(context, R.layout.list_item, itemsList);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null || !(convertView.getTag() instanceof ViewHolder)) {
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            convertView = mInflater.inflate( R.layout.list_item, null);
            //Creo un viewHolder
            holder = new ViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.itemTitle);
            holder.price = (TextView)convertView.findViewById(R.id.itemPrice);
            holder.subtitle = (TextView)convertView.findViewById(R.id.itemSubtitle);
            holder.quantity = (TextView)convertView.findViewById(R.id.itemQuantity);
            //Guardo holder
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //Obtengo el Item actual
        Item item = getItem(position);

        //Lleno los textView con al info correspondiente del item.
        holder.title.setText(item.getTitle());
        holder.price.setText(getContext().getResources().getString(R.string.currency_symbol) + item.getPrice());
        //Si existen los textViews -> estoy en landscape, cargo la info adicional.
        if(holder.subtitle != null && item.getSubtitle() != null) {
            holder.subtitle.setText(item.getSubtitle());
        }
        if(holder.quantity != null) {
            holder.quantity.setText(item.getAvailableQuantity() + getContext().getResources().getString(R.string.in_stock));
        }
        //Devuelvo la vista cargada.
        return convertView;
    }

    private static class ViewHolder {
        public TextView title;
        public TextView price;
        public TextView subtitle;
        public TextView quantity;
    }
}
