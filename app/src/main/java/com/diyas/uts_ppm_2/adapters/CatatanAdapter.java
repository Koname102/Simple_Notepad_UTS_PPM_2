package com.diyas.uts_ppm_2.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diyas.uts_ppm_2.R;
import com.diyas.uts_ppm_2.models.Catatan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CatatanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Object> items = new ArrayList<>();
    private final Set<Catatan> selectedItems = new HashSet<>();
    private boolean isSelectionMode = false;

    private OnSelectionChangeListener selectionChangeListener;
    private OnItemClickListener itemClickListener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public interface OnSelectionChangeListener {
        void onSelectionChanged(boolean isSelectionMode);
    }

    public interface OnItemClickListener {
        void onItemClick(Catatan catatan);
    }

    public CatatanAdapter(List<Catatan> catatanList) {
        groupByDate(catatanList);
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    private void groupByDate(List<Catatan> catatanList) {
        String lastDate = null;
        for (Catatan catatan : catatanList) {
            String currentDate = catatan.getTanggal();
            if (currentDate == null || currentDate.isEmpty()) {
                currentDate = "Unknown Date"; // Default jika tanggal null
            }
            if (!currentDate.equals(lastDate)) {
                items.add(currentDate); // Tambahkan header
                lastDate = currentDate;
            }
            items.add(catatan); // Tambahkan item
        }
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind((String) items.get(position));
        } else if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind((Catatan) items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Catatan> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void clearSelection() {
        selectedItems.clear();
        exitSelectionMode();
        notifyDataSetChanged();
    }

    private void enterSelectionMode() {
        isSelectionMode = true;
        notifySelectionChanged();
        notifyDataSetChanged();
    }

    private void exitSelectionMode() {
        isSelectionMode = false;
        notifySelectionChanged();
        notifyDataSetChanged();
    }

    private void toggleSelectionMode() {
        if (selectedItems.isEmpty()) {
            exitSelectionMode();
        } else {
            enterSelectionMode();
        }
    }

    private void notifySelectionChanged() {
        if (selectionChangeListener != null) {
            selectionChangeListener.onSelectionChanged(isSelectionMode);
        }
    }

    public void updateData(List<Catatan> newCatatanList) {
        items.clear();
        selectedItems.clear();
        groupByDate(newCatatanList);
        notifyDataSetChanged();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView headerText;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerText = itemView.findViewById(R.id.header_date);
        }

        void bind(String date) {
            headerText.setText(date);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView content;
        private final CheckBox checkbox;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
            content = itemView.findViewById(R.id.item_content);
            checkbox = itemView.findViewById(R.id.item_checkbox);

            // Long press untuk masuk mode seleksi
            itemView.setOnLongClickListener(v -> {
                if (!isSelectionMode) {
                    enterSelectionMode();
                }
                toggleItemSelection();
                return true;
            });

            // Klik item untuk membuka NotepadFragment jika tidak dalam mode seleksi
            itemView.setOnClickListener(v -> {
                if (isSelectionMode) {
                    toggleItemSelection();
                } else if (itemClickListener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick((Catatan) items.get(position));
                    }
                }
            });

            // Klik checkbox untuk memilih/deselect
            checkbox.setOnClickListener(v -> toggleItemSelection());
        }

        void bind(Catatan catatan) {
            title.setText(catatan.getJudul());
            content.setText(catatan.getIsi());
            checkbox.setChecked(selectedItems.contains(catatan));
            checkbox.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);
        }

        private void toggleItemSelection() {
            int position = getBindingAdapterPosition();

            if (position == RecyclerView.NO_POSITION || position >= items.size() || position < 0) {
                return;
            }

            Object item = items.get(position);
            if (item instanceof Catatan) {
                Catatan catatan = (Catatan) item;
                if (selectedItems.contains(catatan)) {
                    selectedItems.remove(catatan);
                } else {
                    selectedItems.add(catatan);
                }
                toggleSelectionMode();
                notifyItemChanged(position);
            }
        }
    }
}
