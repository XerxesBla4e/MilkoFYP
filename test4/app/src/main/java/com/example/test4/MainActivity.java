package com.example.test4;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.test4.BluetoothConnect.SelectDevice;
import com.example.test4.Databases.DatabaseManager;
import com.example.test4.LogSign.Login;
import com.example.test4.Sessions.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLDataException;
import java.util.Set;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView dealername;
    ImageView analyse, records, logout;
    ProgressBar progressBar;
    Button connectBT;
    DatabaseManager databaseManager;

    private String deviceName = null;
    private String deviceAddress;
    public static Handler handler;
    BluetoothDevice device;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public static CreateConnectThread createConnectThread;

    private final static int CONNECTING_STATUS = 1; // Used in bluetooth handler to identify message status
    private final static int MESSAGE_READ = 2; // Used in bluetooth handler to identify message update
    Set<BluetoothDevice> pairedDevices;
    BluetoothAdapter bluetoothAdapter;

    int PERMISSION_REQUEST_CODE = 454;
    private static String[] BTPERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main2);

        initViews();
        checkGrantPerms();

        databaseManager = new DatabaseManager(getApplicationContext());
        try {
            databaseManager.open();
        } catch (SQLDataException e) {
            e.printStackTrace();
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT
                }, 3);
                return;
            }
        }
        pairedDevices = bluetoothAdapter.getBondedDevices();

        setClickables();
        connectToDevice();
        GUihandler();
    }

    private void initViews() {
        // Initialization
        connectBT = findViewById(R.id.btn3);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        analyse = findViewById(R.id.analyse);
        records = findViewById(R.id.records);
        dealername = findViewById(R.id.name);
        logout = findViewById(R.id.logout);
    }

    private void checkGrantPerms() {
        if (!hasPermissions(this, BTPERMISSIONS)) {
            ActivityCompat.requestPermissions(this, BTPERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setClickables() {
        connectBT.setOnClickListener(this);
        logout.setOnClickListener(this);
        analyse.setOnClickListener(this);
        records.setOnClickListener(this);
    }

    private void GUihandler() {
        // GUI Handler
        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case CONNECTING_STATUS:
                        dealername.setText("Connected to " + deviceName);
                        progressBar.setVisibility(View.GONE);
                        connectBT.setEnabled(true);
                        break;
                    case -1:
                        dealername.setText("Connection Failed");
                        progressBar.setVisibility(View.GONE);
                        connectBT.setEnabled(true);
                        break;
                    case MESSAGE_READ:
                        // Read milk pH result from Arduino
                        String pHres = msg.obj.toString();
                        Float finalres = Float.parseFloat(pHres);
                        dealername.setText("pH: " + finalres);

                        Intent i = new Intent(getApplicationContext(), Results.class);
                        i.putExtra("pH", finalres);
                        startActivity(i);
                        break;
                }
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private void connectToDevice() {
        deviceName = getIntent().getStringExtra("deviceName");

        if (deviceName != null) {
            deviceAddress = getIntent().getStringExtra("deviceAddress");

            progressBar.setVisibility(View.VISIBLE);
            dealername.setText("Connecting to " + deviceName);
            connectBT.setEnabled(false);

            // Create thread to create a Bluetooth connection to the selected device
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceAddress);
            createConnectThread.start();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        int btnid = view.getId();
        switch (btnid) {
            case R.id.btn3:
                Intent intent = new Intent(MainActivity.this, SelectDevice.class);
                startActivity(intent);
                break;
            case R.id.analyse:
                if (pairedDevices.size() > 0) {
                    String cmdText = "1";
                    connectedThread.write(cmdText);
                } else {
                    Snackbar snackbar = Snackbar.make(view, "Activate Or Pair Bluetooth Device", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent xer1 = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                            startActivityForResult(xer1, 1);
                        }
                    });
                    snackbar.show();
                }
                break;
            case R.id.logout:
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                sessionManager.removeSession();
                moveToLogin();
                break;
            case R.id.records:
                Intent intent2 = new Intent(MainActivity.this, RecordsActivity.class);
                startActivity(intent2);
                break;
        }
    }

    private void moveToLogin() {
        Intent xerxes = new Intent(MainActivity.this, Login.class);
        xerxes.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(xerxes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (createConnectThread != null) {
            createConnectThread.cancel();
        }
        if (connectedThread != null) {
            connectedThread.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        if (createConnectThread != null) {
            createConnectThread.cancel();
        }
        Intent xer = new Intent(Intent.ACTION_MAIN);
        xer.addCategory(Intent.CATEGORY_HOME);
        xer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(xer);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with Bluetooth logic
            } else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class CreateConnectThread extends Thread {
        @SuppressLint("MissingPermission")
        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmpobj = null;

            UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                tmpobj = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmpobj;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            bluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
                Log.e(TAG, "Device connected");
                handler.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                    Log.e(TAG, "Cannot connect to device");
                    handler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the socket", e);
            }
        }
    }

    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpInObj = null;
            OutputStream tmpOutObj = null;

            try {
                tmpInObj = socket.getInputStream();
                tmpOutObj = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input/output stream", e);
            }
            mmInStream = tmpInObj;
            mmOutStream = tmpOutObj;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes = 0;

            while (true) {
                try {
                    buffer[bytes] = (byte) mmInStream.read();
                    String readMessage;
                    if (buffer[bytes] == '\n') {
                        readMessage = new String(buffer, 0, bytes);
                        Log.e("Arduino Message", readMessage);
                        handler.obtainMessage(MESSAGE_READ, readMessage).sendToTarget();
                        bytes = 0;
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        public void write(String input) {
            if (mmOutStream != null) {
                byte[] bytes = input.getBytes();
                try {
                    mmOutStream.write(bytes);
                } catch (IOException e) {
                    Log.e(TAG, "Error occurred when sending data", e);
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
