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

public class uebungscreator_ElementAdapter extends RecyclerView.Adapter<uebungscreator_ElementAdapter.ViewHolder> {

    private List<String> elements;
    private Context context;
    private List<String> disabledElements = new ArrayList<>();

    public uebungscreator_ElementAdapter(List<String> elements, Context context) {
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

        // If the element is disabled, apply a semi-transparent background
        if (disabledElements.contains(element)) {
            // Set a semi-transparent background to grey out the element
            holder.textView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));

            // Make the element semi-transparent and disabled
            holder.textView.setAlpha(0.5f); // Apply opacity (you can adjust the value)
            holder.textView.setEnabled(false); // Disable interactions
        } else {
            // Set default background (transparent or a clear background)
            holder.textView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

            // Make the element fully interactive
            holder.textView.setEnabled(true);

            // Optional: Restore full opacity if needed (default behavior)
            holder.textView.setAlpha(1.0f);
        }

        // Handle the drag-and-drop functionality
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
    public void updateElements(List<String> newElements) {
        this.elements.clear();
        this.elements.addAll(newElements);
        notifyDataSetChanged(); // Refresh RecyclerView
    }
    public void enableElements(String[] elementsToEnable) {
        for (String element : elementsToEnable) {
            disabledElements.remove(element); // Remove from disabled set
        }
        notifyDataSetChanged(); // Refresh the UI
    }
    public void resetDisabledElements() {
        disabledElements.clear();
        notifyDataSetChanged(); // Refresh the RecyclerView
    }

}


