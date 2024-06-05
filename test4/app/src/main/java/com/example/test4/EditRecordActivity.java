package com.example.test4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class EditRecordActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "com.example.test4.EXTRA_ID";
    public static final String EXTRA_ADDRESS = "com.example.test4.EXTRA_ADDRESS";
    public static final String EXTRA_ADULTERANT = "com.example.test4.EXTRA_ADULTERANT";

    private EditText editTextAddress;
    private EditText editTextAdulterant;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_record);

        editTextAddress = findViewById(R.id.milksource7);
        editTextAdulterant = findViewById(R.id.adulterant7);
        buttonSave = findViewById(R.id.btnUpdate);

        //retrieve available record details
        Intent intent = getIntent();
        if (intent != null) {
            editTextAddress.setText(intent.getStringExtra(EXTRA_ADDRESS));
            editTextAdulterant.setText(intent.getStringExtra(EXTRA_ADULTERANT));
        }

        buttonSave.setOnClickListener(v -> {
            //updated record details
            String address = editTextAddress.getText().toString();
            String adulterant = editTextAdulterant.getText().toString();
            //end

            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_ID, intent.getLongExtra(EXTRA_ID, -1));
            resultIntent.putExtra(EXTRA_ADDRESS, address);
            resultIntent.putExtra(EXTRA_ADULTERANT, adulterant);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}