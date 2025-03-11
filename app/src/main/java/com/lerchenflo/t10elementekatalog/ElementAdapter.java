package com.lerchenflo.t10elementekatalog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ElementAdapter extends RecyclerView.Adapter<ElementAdapter.ViewHolder> {

    private List<String> elements;
    private Context context;
    private List<String> disabledElements = new ArrayList<>();

    public ElementAdapter(List<String> elements, Context context) {
        this.elements = elements;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String element = elements.get(position);
        holder.textView.setText(element);

        if (disabledElements.contains(element)) {
            holder.textView.setAlpha(0.5f); // Grey out disabled elements
            holder.textView.setEnabled(false);
        } else {
            holder.textView.setAlpha(1.0f);
            holder.textView.setEnabled(true);
        }

        holder.textView.setOnLongClickListener(v -> {
            if (!disabledElements.contains(element)) {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(null, shadowBuilder, v, 0);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public void disableElements(String[] group) {
        for (String item : group) {
            if (!disabledElements.contains(item)) {
                disabledElements.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }
}
