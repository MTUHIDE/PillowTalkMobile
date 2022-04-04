package edu.mtu.HIDE.pillowtalkmobile;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.UUID;

import edu.mtu.HIDE.pillowtalkmobile.R.id;

public class MainActivity extends AppCompatActivity implements TestServerConnectionAsyncResponse, POSTRequestAsyncResponse {
    public enum PillowID {
        cushion_1(1, 2),
        cushion_2(3, 4);

        int inflate;
        int deflate;

        PillowID(int inflate, int deflate)
        {
            this.inflate = inflate;
            this.deflate = deflate;
        }
    }

    public enum PillowBaseCommand {
        inflate,
        deflate
    }

    private static final UUID PILLOWTALK_UUID = UUID.fromString("79bf39f7-54a4-4015-b27e-0b4be44b506d");

    //global references
    BluetoothService bluetoothService;

    //UI references
    private TextView serverStatusLabel;
    private TextView pillow_1_label;
    private TextView pillow_2_label;
    private TextView bluetoothStatusLabel;
    private Button stopAllButton;
    private Button pillow1InflateButton;
    private Button pillow1DeflateButton;
    private Button pillow2InflateButton;
    private Button pillow2DeflateButton;
    private RadioGroup pillow1PresetSelection;
    private RadioGroup pillow2PresetSelection;
    private ImageButton openSettingsButton;
    private UserSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize values
        settings = new UserSettings(this);

        //global references
        bluetoothService = new BluetoothService(this, PILLOWTALK_UUID);

        //initialize UI references
        serverStatusLabel = findViewById(id.network_status_label);
        bluetoothStatusLabel = findViewById(id.bluetooth_status_label);

        pillow_1_label = findViewById(R.id.pillow_1_label);
        pillow_2_label = findViewById(R.id.pillow_2_label);

        pillow_1_label.setText(settings.getPillow1Nickname());
        pillow_2_label.setText(settings.getPillow2Nickname());

        final Switch bluetoothSwitch = findViewById(id.use_bluetooth_switch);

        final EditText serverIPEditText = findViewById(id.server_ip_text);

        pillow1InflateButton = findViewById(id.pillow_1_button_increase);
        pillow1DeflateButton = findViewById(id.pillow_1_button_decrease);
        pillow2InflateButton = findViewById(id.pillow_2_button_increase);
        pillow2DeflateButton = findViewById(id.pillow_2_button_decrease);

        pillow1PresetSelection = findViewById(id.pillow_1_preset_selection);
        pillow2PresetSelection = findViewById(id.pillow_2_preset_selection);

        openSettingsButton = findViewById(id.floatingSettingsButton);

        stopAllButton = findViewById(id.stop_all_button);

        settings.setUseBluetooth(false);// BAD FIX
        bluetoothSwitch.setChecked(settings.getUseBluetooth());

        serverIPEditText.setEnabled(!settings.getUseBluetooth()); //disable if using bluetooth
        serverIPEditText.setText(settings.getIPAddress());


        openSettingsButton.setOnClickListener(view -> openSettings(view, settings));

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

        bluetoothService.addMessageListener(() -> runOnUiThread(() -> bluetoothStatusLabel.setText("Bluetooth: " + bluetoothService.latestMessage)));

        stopAllButton.setOnClickListener(view -> {
            if (bluetoothSwitch.isChecked()) {
                bluetoothService.write("{\"motors\" : []}");
            } else {
                stopAllPOST(settings.getIPAddress());
            }
        });

        pillow1InflateButton.setOnClickListener(view -> sendCommand(PillowBaseCommand.inflate, setInterval(PillowID.cushion_1), PillowID.cushion_1));

        pillow1DeflateButton.setOnClickListener(view -> sendCommand(PillowBaseCommand.deflate, setInterval(PillowID.cushion_1), PillowID.cushion_1));

        pillow2InflateButton.setOnClickListener(view -> sendCommand(PillowBaseCommand.inflate, setInterval(PillowID.cushion_1), PillowID.cushion_2));

        pillow2DeflateButton.setOnClickListener(view -> sendCommand(PillowBaseCommand.deflate, setInterval(PillowID.cushion_1), PillowID.cushion_2));

        //Add UI listeners
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            private void connectTo(BluetoothDevice bluetoothDevice, boolean checked) {
                if (bluetoothDevice != null) {
                    settings.setUseBluetooth(checked);
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
            }

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (!bluetoothSwitch.isChecked()) {
                    settings.setUseBluetooth(false);
                    serverIPEditText.setEnabled(true);
                    bluetoothService.stop();
                    return;
                }

                List<BluetoothDevice> devices = bluetoothService.findDevices(PILLOWTALK_UUID);

                if (devices.size() > 1) {
                    String[] deviceNames = new String[devices.size()];
                    for (int i = 0; i < devices.size(); i++) {
                        deviceNames[i] = devices.get(i).getName();
                    }

                    new MaterialAlertDialogBuilder(MainActivity.this)
                            .setTitle("Pick device")
                            .setNegativeButton("Cancel", null)
                            .setSingleChoiceItems(deviceNames, -1, (dialogInterface, i) -> {
                                ListView listView = ((AlertDialog) dialogInterface).getListView();
                                listView.setTag(i);
                            })
                            .setPositiveButton("Connect", (dialogInterface, i) -> {
                                ListView listView = ((AlertDialog) dialogInterface).getListView();
                                Integer selected = (Integer) listView.getTag();
                                if (selected != null) {
                                    connectTo(devices.get(selected), checked);
                                }
                            })
                            .show();

                } else {
                    connectTo(devices.get(0), checked);
                }
            }
        });

        serverIPEditText.addTextChangedListener(new TextChangedListener<EditText>(serverIPEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                settings.setIPAddress(target.getText().toString());
                testHttpsServerConnectivity(settings.getIPAddress()); //retest connection
            }
        });

        //Run functions
        testHttpsServerConnectivity(settings.getIPAddress());
    }

    private int setInterval(PillowID pillowID) {
        // selects the first pillow radio group or the second pillow radio group based on pillow id
        RadioGroup selection; // = (pillowID == PillowID.cushion_1 ? pillow1PresetSelection : pillow2PresetSelection);

        //
        if (pillowID == PillowID.cushion_1) {
            selection = pillow1PresetSelection;
            switch (selection.getCheckedRadioButtonId()) {
                case id.pillow_1_preset_low:
                    return settings.getPillow1LowPressureInterval();
                case id.pillow_1_preset_medium:
                    return settings.getPillow1MediumPressureInterval();
                case id.pillow_1_preset_high:
                    return settings.getPillow1HighPressureInterval();
                default:
                    return 0;
            }
        } else {
            selection = pillow2PresetSelection;
            switch (selection.getCheckedRadioButtonId()) {
                case id.pillow_2_preset_low:
                    return settings.getPillow2LowPressureInterval();
                case id.pillow_2_preset_medium:
                    return settings.getPillow2MediumPressureInterval();
                case id.pillow_2_preset_high:
                    return settings.getPillow2HighPressureInterval();
                default:
                    return 0;
            }
        }
    }

    private void openSettings(View view, UserSettings settings) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivityForResult(settingsIntent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pillow_1_label.setText(settings.getPillow1Nickname());
        pillow_2_label.setText(settings.getPillow2Nickname());

        Log.d("RETURNING", "activity result " + resultCode);

    }

    private void sendCommand(PillowBaseCommand command, int interval, PillowID id) {
        if (settings.getUseBluetooth()) {
            sendBluetoothCommand(command, interval, id);
        } else {
            sendPOSTRequest(settings.getIPAddress(), command, interval, id);
        }
    }

    private void sendPOSTRequest(String ip, PillowBaseCommand command, int duration, PillowID pillowID) {
        if (ip.isEmpty()) return;

        String com = buildPOSTRequestCommand(command, duration, pillowID);

        serverStatusLabel.setText("Server: Trying to send command");

        String address = "http://" + ip + ":3000/motorcontrol";
        Log.d("TESTING", "Trying: " + address);

        POSTRequestTask postRequestTask = new POSTRequestTask();
        postRequestTask.delegate = this;
        postRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, address, com);
    }

    private void stopAllPOST(String ip) {
        String address = "http://" + ip + ":3000/motorcontrol";
        Log.d("TESTING", "Trying: " + address);

        String com = "{\n" +
                "    \"motors\" : []\n" +
                "}";

        POSTRequestTask postRequestTask = new POSTRequestTask();
        postRequestTask.delegate = this;
        postRequestTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, address, com);
    }

    private String buildPOSTRequestCommand(PillowBaseCommand base, int baseParameter, PillowID pillowID) {

        String ret = "{" +
                "    \"motors\" : [" +
                "        {\"motor\": " + (base == PillowBaseCommand.inflate ? pillowID.inflate : pillowID.deflate) + ", \"time\": " + baseParameter + "}" +
                "    ]" +
                "}";

        Log.d("SENDING REQUEST::", ret);
        return ret;
    }

    private void sendBluetoothCommand(PillowBaseCommand command, int duration, PillowID pillowID) {
        String str = buildPOSTRequestCommand(command, duration, pillowID);
        bluetoothService.write(str);
    }

    private void testHttpsServerConnectivity(String ip) {
        disableButtonControl();

        if (ip.isEmpty()) return;

        serverStatusLabel.setText("Server: Trying to Connect");

        String address1 = "http://" + ip + ":3000/healthcheck"; //external
        Log.d("TESTING", "Trying: " + address1);

        //must initialize new instance of task
        TestServerConnectionTask testServerConnectionTask = new TestServerConnectionTask();
        testServerConnectionTask.delegate = this;
        testServerConnectionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, address1);
    }

    void enableButtonControl() {
        pillow1InflateButton.setEnabled(true);
        pillow1DeflateButton.setEnabled(true);
        pillow2InflateButton.setEnabled(true);
        pillow2DeflateButton.setEnabled(true);

        pillow1PresetSelection.setEnabled(true);
        pillow2PresetSelection.setEnabled(true);

        stopAllButton.setEnabled(true);
    }

    void disableButtonControl() {
        pillow1InflateButton.setEnabled(false);
        pillow1DeflateButton.setEnabled(false);
        pillow2InflateButton.setEnabled(false);
        pillow2DeflateButton.setEnabled(false);

        pillow1PresetSelection.setEnabled(false);
        pillow2PresetSelection.setEnabled(false);

        stopAllButton.setEnabled(false);
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
            // enableButtonControl();
        } else {
            serverStatusLabel.setText("Server: Failed to Connect");
            disableButtonControl();
        }
    }
}
