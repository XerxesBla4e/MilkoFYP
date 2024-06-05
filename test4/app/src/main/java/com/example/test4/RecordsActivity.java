package com.example.test4;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test4.Adapters.RecordAdapter;
import com.example.test4.Databases.DatabaseManager;
import com.example.test4.Models.Record;
import com.example.test4.litener.OnItemClickListener;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

public class RecordsActivity extends AppCompatActivity implements OnItemClickListener {

    private static final int EDIT_RECORD_REQUEST = 1;
    private RecyclerView recyclerView;
    private RecordAdapter adapter;
    private List<Record> records;
    private List<Record> Retrievedrecords;
    DatabaseManager databaseManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        initViews();

        try {
            databaseManager.open();
        } catch (SQLDataException e) {
            throw new RuntimeException(e);
        }

        records = new ArrayList<>();
        Retrievedrecords = databaseManager.retrieveAllRecords();
        if (Retrievedrecords != null) {
            records.addAll(Retrievedrecords);
        }

        adapter = new RecordAdapter(this, records);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseManager.close();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.adulterantrecycler2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseManager = new DatabaseManager(this);
    }

    @Override
    public void onEditClick(long id) {
        Record record = databaseManager.retrieveRecord(id);
        if (record != null) {
            Intent intent = new Intent(this, EditRecordActivity.class);
            intent.putExtra(EditRecordActivity.EXTRA_ID, record.getId());
            intent.putExtra(EditRecordActivity.EXTRA_ADDRESS, record.getId());
            intent.putExtra(EditRecordActivity.EXTRA_ADULTERANT, record.getId());
            startActivityForResult(intent, EDIT_RECORD_REQUEST);//set the request code ,1

        }

    }

    @Override
    public void onDeleteClick(long id) {
        int deleteResult = databaseManager.delete(id);
        if (deleteResult > 0) {
            Toast.makeText(getApplicationContext(), "Record deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to delete record", Toast.LENGTH_SHORT).show();
        }
        List<Record> records1 = databaseManager.retrieveAllRecords();
        adapter.updateRecord(records1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_RECORD_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            long id = data.getLongExtra(EditRecordActivity.EXTRA_ID, -1);
            String address = data.getStringExtra(EditRecordActivity.EXTRA_ADDRESS);
            String adulterant = data.getStringExtra(EditRecordActivity.EXTRA_ADULTERANT);

            int updateResult = databaseManager.updateRecord(id, address, adulterant);

            if (updateResult > 0) {
                Toast.makeText(getApplicationContext(), "Record updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Failed to update record", Toast.LENGTH_SHORT).show();
            }

            List<Record> records = databaseManager.retrieveAllRecords();
            adapter.updateRecord(records);
        }
    }
}
