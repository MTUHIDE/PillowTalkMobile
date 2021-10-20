package edu.mtu.HIDE.pillowtalkmobile;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements TestServerConnectionAsyncResponse, POSTRequestAsyncResponse {

    private static final String BLUETOOTH_DEVICE = "LAPTOP-5T6R7RAM";

    //global references
    BluetoothService bluetoothService;

    //UI references
    private TextView serverStatusLabel;
    private TextView bluetoothStatusLabel;
    private Button pillow1InflateButton;
    private Button pillow1DeflateButton;
    private Button pillow2InflateButton;
    private Button pillow2DeflateButton;
    private RadioButton pillow1PresetLow;
    private RadioButton pillow1PresetMedium;
    private RadioButton pillow1PresetHigh;
    private RadioButton pillow2PresetLow;
    private RadioButton pillow2PresetMedium;
    private RadioButton pillow2PresetHigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //global references
        bluetoothService = new BluetoothService(this);

        //initialize UI references
        serverStatusLabel = findViewById(R.id.network_status_label);
        bluetoothStatusLabel = findViewById(R.id.bluetooth_status_label);

        final Switch bluetoothSwitch = findViewById(R.id.use_bluetooth_switch);

        final EditText serverIPEditText = findViewById(R.id.server_ip_text);

        EditText pillow1NicknameEditText = findViewById(R.id.pillow_1_nickname_text);
        EditText pillow2NicknameEditText = findViewById(R.id.pillow_2_nickname_text);

        EditText pillow1LowInflateEditText = findViewById(R.id.pillow_1_pressure_interval_low_text);
        EditText pillow2LowInflateEditText = findViewById(R.id.pillow_2_pressure_interval_low_text);
        EditText pillow1MediumInflateEditText = findViewById(R.id.pillow_1_pressure_interval_medium_text);
        EditText pillow2MediumInflateEditText = findViewById(R.id.pillow_2_pressure_interval_medium_text);
        EditText pillow1HighInflateEditText = findViewById(R.id.pillow_1_pressure_interval_high_text);
        EditText pillow2HighInflateEditText = findViewById(R.id.pillow_2_pressure_interval_high_text);

        pillow1InflateButton = findViewById(R.id.pillow_1_button_increase);
        pillow1DeflateButton = findViewById(R.id.pillow_1_button_decrease);
        pillow2InflateButton = findViewById(R.id.pillow_2_button_increase);
        pillow2DeflateButton = findViewById(R.id.pillow_2_button_decrease);

        pillow1PresetLow = findViewById(R.id.pillow_1_preset_low);
        pillow1PresetMedium = findViewById(R.id.pillow_1_preset_medium);
        pillow1PresetHigh = findViewById(R.id.pillow_1_preset_high);
        pillow2PresetLow = findViewById(R.id.pillow_2_preset_low);
        pillow2PresetMedium = findViewById(R.id.pillow_2_preset_medium);
        pillow2PresetHigh = findViewById(R.id.pillow_2_preset_high);

        //Initialize values
        final UserSettings settings = new UserSettings(this);

        bluetoothSwitch.setChecked(settings.getUseBluetooth());

        serverIPEditText.setEnabled(!settings.getUseBluetooth()); //disable if using bluetooth
        serverIPEditText.setText(settings.getIPAddress());

        pillow1NicknameEditText.setText(settings.getPillow1Nickname());
        pillow2NicknameEditText.setText(settings.getPillow2Nickname());

        pillow1LowInflateEditText.setText(String.valueOf(settings.getPillow1LowPressureInterval()));
        pillow2LowInflateEditText.setText(String.valueOf(settings.getPillow2LowPressureInterval()));
        pillow1MediumInflateEditText.setText(String.valueOf(settings.getPillow1MediumPressureInterval()));
        pillow2MediumInflateEditText.setText(String.valueOf(settings.getPillow2MediumPressureInterval()));
        pillow1HighInflateEditText.setText(String.valueOf(settings.getPillow1HighPressureInterval()));
        pillow2HighInflateEditText.setText(String.valueOf(settings.getPillow2HighPressureInterval()));


        bluetoothService.addStateListener(() -> runOnUiThread(() -> {
            int newState = bluetoothService.getState();
            switch (newState) {
                case BluetoothService.STATE_CONNECTED:
                    bluetoothStatusLabel.setText("Bluetooth: Connected");
                    enableButtonControl();
                    break;
                case BluetoothService.STATE_NONE:
                    bluetoothStatusLabel.setText("Bluetooth: Not Connected");
                    bluetoothSwitch.setChecked(false);
                    disableButtonControl();
                    break;
                case BluetoothService.STATE_CONNECTING:
                    bluetoothStatusLabel.setText("Bluetooth: Trying to Connect");
                    break;
                case BluetoothService.STATE_LISTEN:
                    bluetoothSwitch.setText("Bluetooth: Listening for Bluetooth Server");
                    break;
            }
        }));

        bluetoothService.addMessageListener(() -> runOnUiThread(() -> {
            bluetoothStatusLabel.setText("Bluetooth: " + bluetoothService.latestMessage);
        }));

        pillow1InflateButton.setOnClickListener(view -> {
            int interval = 0;
            if (pillow1PresetLow.isChecked()) {
                interval = settings.getPillow1LowPressureInterval();
            } else if (pillow1PresetMedium.isChecked()) {
                interval = settings.getPillow1MediumPressureInterval();
            } else if (pillow1PresetHigh.isChecked()) {
                interval = settings.getPillow1HighPressureInterval();
            }


            String command;
            if (settings.getUseBluetooth()) {
                command = buildBluetoothCommand(PillowBaseCommand.inflate, interval + "", PillowID.cushion_1);
                bluetoothService.write(command);
            } else {
                command = buildPOSTRequestCommand(PillowBaseCommand.inflate, interval + "", PillowID.cushion_1);
                sendPOSTRequest(settings.getIPAddress(), command);
            }
        });

        pillow1DeflateButton.setOnClickListener(view -> {
            int interval = 0;
            if (pillow1PresetLow.isChecked()) {
                interval = settings.getPillow1LowPressureInterval();
            } else if (pillow1PresetMedium.isChecked()) {
                interval = settings.getPillow1MediumPressureInterval();
            } else if (pillow1PresetHigh.isChecked()) {
                interval = settings.getPillow1HighPressureInterval();
            }


            String command;
            if (settings.getUseBluetooth()) {
                command = buildBluetoothCommand(PillowBaseCommand.deflate, interval + "", PillowID.cushion_1);
                bluetoothService.write(command);
            } else {
                command = buildPOSTRequestCommand(PillowBaseCommand.deflate, interval + "", PillowID.cushion_1);
                sendPOSTRequest(settings.getIPAddress(), command);
            }
        });

        pillow2InflateButton.setOnClickListener(view -> {
            int interval = 0;
            if (pillow2PresetLow.isChecked()) {
                interval = settings.getPillow2LowPressureInterval();
            } else if (pillow2PresetMedium.isChecked()) {
                interval = settings.getPillow2MediumPressureInterval();
            } else if (pillow2PresetHigh.isChecked()) {
                interval = settings.getPillow2HighPressureInterval();
            }


            String command;
            if (settings.getUseBluetooth()) {
                command = buildBluetoothCommand(PillowBaseCommand.inflate, interval + "", PillowID.cushion_2);
                bluetoothService.write(command);
            } else {
                command = buildPOSTRequestCommand(PillowBaseCommand.inflate, interval + "", PillowID.cushion_2);
                sendPOSTRequest(settings.getIPAddress(), command);
            }
        });

        pillow2DeflateButton.setOnClickListener(view -> {
            int interval = 0;
            if (pillow2PresetLow.isChecked()) {
                interval = settings.getPillow2LowPressureInterval();
            } else if (pillow2PresetMedium.isChecked()) {
                interval = settings.getPillow2MediumPressureInterval();
            } else if (pillow2PresetHigh.isChecked()) {
                interval = settings.getPillow2HighPressureInterval();
            }

            String command;
            if (settings.getUseBluetooth()) {
                command = buildBluetoothCommand(PillowBaseCommand.deflate, interval + "", PillowID.cushion_2);
                bluetoothService.write(command);
            } else {
                command = buildPOSTRequestCommand(PillowBaseCommand.deflate, interval + "", PillowID.cushion_2);
                sendPOSTRequest(settings.getIPAddress(), command);
            }
        });

        //Add UI listeners
        bluetoothSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!bluetoothSwitch.isChecked()) {
                settings.setUseBluetooth(false);
                serverIPEditText.setEnabled(true);
                bluetoothService.stop();
                return;
            }

            BluetoothDevice bluetoothDevice = bluetoothService.findDevice(BLUETOOTH_DEVICE);
            if (bluetoothDevice != null) {
                settings.setUseBluetooth(b);
                serverIPEditText.setEnabled(!settings.getUseBluetooth()); //disable if using bluetooth

                bluetoothService.enableBluetooth();
                bluetoothService.connect(bluetoothDevice);
                Log.d("TESTING", "mainactivity getting bluetooth state: " + bluetoothService.getState());
            } else {
                bluetoothSwitch.setChecked(false);
                settings.setUseBluetooth(false);
                serverIPEditText.setEnabled(true);

                Log.d("TESTING", "MainActivity - Bluetooth device not found");
                bluetoothStatusLabel.setText("Bluetooth: Bluetooth device not found");
            }

        });

        serverIPEditText.addTextChangedListener(new TextChangedListener<EditText>(serverIPEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                settings.setIPAddress(target.getText().toString());
                testHttpsServerConnectivity(settings.getIPAddress()); //retest connection
            }
        });

        pillow1NicknameEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow1NicknameEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow1Nickname(newValue);
                else settings.setPillow1Nickname("Pillow 1");
            }
        });

        pillow2NicknameEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow2NicknameEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow2Nickname(newValue);
                else settings.setPillow2Nickname("Pillow 2");
            }
        });

        pillow1LowInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow1LowInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty())
                    settings.setPillow1LowPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow1LowPressureInterval(1);
            }
        });

        pillow2LowInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow2LowInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty())
                    settings.setPillow2LowPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow2LowPressureInterval(1);
            }
        });

        pillow1MediumInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow1MediumInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty())
                    settings.setPillow1MediumPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow1MediumPressureInterval(2);
            }
        });

        pillow2MediumInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow2MediumInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty())
                    settings.setPillow2MediumPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow2MediumPressureInterval(2);
            }
        });

        pillow1HighInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow1HighInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty())
                    settings.setPillow1HighPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow1HighPressureInterval(3);
            }
        });

        pillow2HighInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow2HighInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty())
                    settings.setPillow2HighPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow2HighPressureInterval(3);
            }
        });

        //Run functions
        testHttpsServerConnectivity(settings.getIPAddress());
    }

    private void sendPOSTRequest(String ip, String command) {
        disableButtonControl();

        if (ip.isEmpty()) return;

        serverStatusLabel.setText("Server: Trying to send command");

        String address = "http://" + ip + ":443/command";
        Log.d("TESTING", "Trying: " + address);

        POSTRequestTask postRequestTask = new POSTRequestTask();
        postRequestTask.delegate = this;
        postRequestTask.execute(address, command);
    }

    private String buildPOSTRequestCommand(PillowBaseCommand base, String baseParameter, PillowID pillowID) {
        //example: base = inflate, baseParameter = 5 (secs) ,  pillowID  = cushion_1
        //expected format = "command=inflate%20cushion_1%206"

        return "command=" + base + "%20" + pillowID + "%20" + baseParameter;
    }

    private String buildBluetoothCommand(PillowBaseCommand base, String baseParameter, PillowID pillowID) {
        return base + " " + pillowID + " " + baseParameter;
    }

    private void testHttpsServerConnectivity(String ip) {
        disableButtonControl();

        if (ip.isEmpty()) return;

        serverStatusLabel.setText("Server: Trying to Connect");

        String address1 = "http://" + ip + ":443/server_connection_test"; //external
        Log.d("TESTING", "Trying: " + address1);

        //must initialize new instance of task
        TestServerConnectionTask testServerConnectionTask = new TestServerConnectionTask();
        testServerConnectionTask.delegate = this;
        testServerConnectionTask.execute(address1);
    }

    void enableButtonControl() {
        pillow1InflateButton.setEnabled(true);
        pillow1DeflateButton.setEnabled(true);
        pillow2InflateButton.setEnabled(true);
        pillow2DeflateButton.setEnabled(true);

        pillow1PresetLow.setEnabled(true);
        pillow1PresetMedium.setEnabled(true);
        pillow1PresetHigh.setEnabled(true);
        pillow2PresetLow.setEnabled(true);
        pillow2PresetMedium.setEnabled(true);
        pillow2PresetHigh.setEnabled(true);
    }

    void disableButtonControl() {
        pillow1InflateButton.setEnabled(false);
        pillow1DeflateButton.setEnabled(false);
        pillow2InflateButton.setEnabled(false);
        pillow2DeflateButton.setEnabled(false);

        pillow1PresetLow.setEnabled(false);
        pillow1PresetMedium.setEnabled(false);
        pillow1PresetHigh.setEnabled(false);
        pillow2PresetLow.setEnabled(false);
        pillow2PresetMedium.setEnabled(false);
        pillow2PresetHigh.setEnabled(false);
    }

    @Override
    public void testServerConnectionTaskResponse(String results) {
        //200 = Good HTTPS response code
        if (results.equals("200 OK")) {
            serverStatusLabel.setText("Server: Connected");
            enableButtonControl();
        } else {
            serverStatusLabel.setText("Server: Failed to Connect");
            disableButtonControl();
        }
    }

    @Override
    public void POSTRequestTaskResponse(String results) {
        //200 = Good HTTPS response code
        if (results.equals("200 OK")) {
            serverStatusLabel.setText("Server: Command Executed");
            enableButtonControl();
        } else {
            serverStatusLabel.setText("Server: Failed to Connect");
            disableButtonControl();
        }
    }
}
