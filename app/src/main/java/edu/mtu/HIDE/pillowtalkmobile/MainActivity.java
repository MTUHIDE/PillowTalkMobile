package edu.mtu.HIDE.pillowtalkmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements TestServerConnectionAsyncResponse {

    //UI references
    private TextView serverStatusLabel;
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

        //initialize UI references
        serverStatusLabel = findViewById(R.id.network_status_label);

        Switch bluetoothSwitch = findViewById(R.id.use_bluetooth_switch);

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

        //Add UI listeners
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settings.setUseBluetooth(b);
                serverIPEditText.setEnabled(!settings.getUseBluetooth()); //disable if using bluetooth
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
                if (!newValue.isEmpty()) settings.setPillow1LowPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow1LowPressureInterval(1);
            }
        });

        pillow2LowInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow2LowInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow2LowPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow2LowPressureInterval(1);
            }
        });

        pillow1MediumInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow1MediumInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow1MediumPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow1MediumPressureInterval(2);
            }
        });

        pillow2MediumInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow2MediumInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow2MediumPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow2MediumPressureInterval(2);
            }
        });

        pillow1HighInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow1HighInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow1HighPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow1HighPressureInterval(3);
            }
        });

        pillow2HighInflateEditText.addTextChangedListener(new TextChangedListener<EditText>(pillow2HighInflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow2HighPressureInterval(Integer.parseInt(newValue));
                else settings.setPillow2HighPressureInterval(3);
            }
        });

        //Run functions
        testHttpsServerConnectivity(settings.getIPAddress());
    }

    private void testHttpsServerConnectivity(String ip)
    {
        disableButtonControl();

        if (ip.isEmpty()) return;

        serverStatusLabel.setText("Server Status: Trying to Connect");

        String address1 = "http://" + ip + ":443/server_connection_test"; //external
        Log.d("TESTING", "Trying: " + address1);

        //must initialize new instance of task
        TestServerConnectionTask testServerConnectionTask = new TestServerConnectionTask();
        testServerConnectionTask.delegate = this;
        testServerConnectionTask.execute(address1);
    }

    void enableButtonControl()
    {
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

    void disableButtonControl()
    {
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
        if (results.equals("200 OK"))
        {
            serverStatusLabel.setText("Server Status: Connected");
            enableButtonControl();
        }
        else
        {
            serverStatusLabel.setText("Server Status: Failed to Connect");
            disableButtonControl();
        }
    }
}
