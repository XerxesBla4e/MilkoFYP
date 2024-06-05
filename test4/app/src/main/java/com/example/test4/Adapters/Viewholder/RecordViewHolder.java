package com.example.test4.Adapters.Viewholder;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test4.R;

public class RecordViewHolder extends RecyclerView.ViewHolder {
    public TextView addressTextView;
    public TextView adulterantTextView;
    public Button editButton;
    public Button deleteButton;

    public RecordViewHolder(@NonNull View itemView) {
        super(itemView);
        addressTextView = itemView.findViewById(R.id.address5);
        adulterantTextView = itemView.findViewById(R.id.adulterant1);
        editButton = itemView.findViewById(R.id.btnedit);
        deleteButton = itemView.findViewById(R.id.btndelete);
    }
}
