package com.example.diariodebolso.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diariodebolso.R;
import com.example.diariodebolso.model.DiaryEntry;
import com.example.diariodebolso.view.EditEntryActivity;
import com.example.diariodebolso.view.ShowItensActivity;
import java.util.List;

public class DiaryEntryAdapter extends RecyclerView.Adapter<DiaryEntryAdapter.EntryViewHolder> {

    private List<DiaryEntry> entryList;
    private Context context;

    public DiaryEntryAdapter(List<DiaryEntry> entryList) {
        this.entryList = entryList;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_diary_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        DiaryEntry entry = entryList.get(position);
        holder.title.setText(entry.getTitle());
        holder.date.setText(entry.getDate());

        if (entry.getContent() != null && !entry.getContent().isEmpty()) {
            holder.contentPreview.setText(entry.getContent());
            holder.contentPreview.setVisibility(View.VISIBLE);
        } else {
            holder.contentPreview.setVisibility(View.GONE);
        }

        if (entry.getPhotoPath() != null && !entry.getPhotoPath().isEmpty()) {
            holder.thumbnail.setImageURI(Uri.parse(entry.getPhotoPath()));
            holder.thumbnail.setVisibility(View.VISIBLE);
        } else {
            holder.thumbnail.setVisibility(View.GONE);
        }

        holder.buttonView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowItensActivity.class);
            intent.putExtra("ENTRY_ID", entry.getId());
            context.startActivity(intent);
        });

        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditEntryActivity.class);
            intent.putExtra("ENTRY_ID", entry.getId());
            context.startActivity(intent);
        });
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
        TextView contentPreview;
        ImageView thumbnail;
        Button buttonEdit;
        Button buttonView;

        EntryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewEntryTitle);
            date = itemView.findViewById(R.id.textViewEntryDate);
            contentPreview = itemView.findViewById(R.id.textViewContentPreview);
            thumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonView = itemView.findViewById(R.id.buttonView);
        }
    }
}