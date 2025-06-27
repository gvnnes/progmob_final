package com.example.diariodebolso.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diariodebolso.R;
import com.example.diariodebolso.model.DiaryEntry;
import java.util.List;

public class DiaryEntryAdapter extends RecyclerView.Adapter<DiaryEntryAdapter.EntryViewHolder> {

    private List<DiaryEntry> entryList;

    public DiaryEntryAdapter(List<DiaryEntry> entryList) {
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        DiaryEntry entry = entryList.get(position);
        holder.title.setText(entry.getTitle());
        holder.date.setText(entry.getDate());
    }

    @Override
    public int getItemCount() {
        return entryList.size();
    }

    public void updateEntries(List<DiaryEntry> newEntries) {
        this.entryList.clear();
        this.entryList.addAll(newEntries);
        notifyDataSetChanged();
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;

        EntryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewEntryTitle);
            date = itemView.findViewById(R.id.textViewEntryDate);
        }
    }
}