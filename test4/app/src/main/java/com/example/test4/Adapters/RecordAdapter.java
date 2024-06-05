package com.example.test4.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test4.Adapters.Viewholder.RecordViewHolder;
import com.example.test4.Models.Record;
import com.example.test4.R;
import com.example.test4.litener.OnItemClickListener;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {
    private Context context;
    private List<Record> records;
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecordAdapter(Context context, List<Record> records) {
        this.context = context;
        this.records = records;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recrow, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = records.get(position);
        holder.addressTextView.setText(record.getSourceAddress());
        holder.adulterantTextView.setText(record.getAdulterant());

        holder.editButton.setOnClickListener(v ->
        {
            if (listener != null) {
                listener.onEditClick(record.getId());
            }
        });
        holder.deleteButton.setOnClickListener(v ->
        {
            if (listener != null) {
                listener.onDeleteClick(record.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void updateRecord(List<Record> newRecords) {
        records = newRecords;
        notifyDataSetChanged();
    }
}
