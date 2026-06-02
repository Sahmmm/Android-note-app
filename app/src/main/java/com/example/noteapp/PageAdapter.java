package com.example.noteapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageHolder> {
    private List<Page> pages = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public PageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_page_item, parent, false);
        return new PageHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PageHolder holder, int position) {
        Page currentPage = pages.get(position);
        holder.titleView.setText(currentPage.getTitle());
        holder.pageIconView.setText(currentPage.getIcon());

        // Conversion du timestamp en date/heure lisible
        Date date = new Date(currentPage.getTimestamp());
        holder.timeView.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date));
        holder.dateView.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date));

        // ✅ La logique de votre PageItemComponent adaptée ici
        try {
            int color = Color.parseColor(currentPage.getColorFont());
            Drawable background = holder.pageContainer.getBackground().mutate();

            if (background instanceof GradientDrawable) {
                GradientDrawable drawable = (GradientDrawable) background;
                drawable.setColor(color); // Applique la couleur de fond
            }
        } catch (Exception e) {
            // Sécurité : si la couleur est mal formatée, on ignore pour éviter un crash
        }
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
        notifyDataSetChanged(); // Met à jour la liste automatiquement
    }

    class PageHolder extends RecyclerView.ViewHolder {
        private TextView titleView, dateView, timeView, pageIconView;
        private ConstraintLayout pageContainer;

        public PageHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.pageTitleView);
            timeView = itemView.findViewById(R.id.pageTimeView);
            dateView = itemView.findViewById(R.id.pageDateView);
            pageIconView = itemView.findViewById(R.id.pageIconView);
            pageContainer = itemView.findViewById(R.id.pageContainer);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(pages.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Page page);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}