package edu.mtu.HIDE.pillowtalkmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //UI references
        Switch bluetoothSwitch = findViewById(R.id.use_bluetooth_switch);

        final EditText serverIPEditText = findViewById(R.id.server_ip_text);

        EditText button1NicknameEditText = findViewById(R.id.pillow_1_nickname_text);
        EditText button2NicknameEditText = findViewById(R.id.pillow_2_nickname_text);

        EditText button1InflateEditText = findViewById(R.id.pillow_1_pressure_interval_text);
        EditText button2InflateEditText = findViewById(R.id.pillow_2_pressure_interval_text);

        //Set user settings
        final UserSettings settings = new UserSettings(this);

        bluetoothSwitch.setChecked(settings.getUseBluetooth());

        serverIPEditText.setEnabled(!settings.getUseBluetooth()); //disable if using bluetooth
        serverIPEditText.setText(settings.getIPAddress());

        button1NicknameEditText.setText(settings.getPillow1Nickname());
        button2NicknameEditText.setText(settings.getPillow2Nickname());

        button1InflateEditText.setText(String.valueOf(settings.getPillow1PressureInterval()));
        button2InflateEditText.setText(String.valueOf(settings.getPillow2PressureInterval()));

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
            }
        });

        button1NicknameEditText.addTextChangedListener(new TextChangedListener<EditText>(button1NicknameEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow1Nickname(newValue);
                else settings.setPillow1Nickname("Pillow 1");
            }
        });

        button2NicknameEditText.addTextChangedListener(new TextChangedListener<EditText>(button2NicknameEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow2Nickname(newValue);
                else settings.setPillow2Nickname("Pillow 2");
            }
        });

        button1InflateEditText.addTextChangedListener(new TextChangedListener<EditText>(button1InflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow1PressureInterval(Integer.parseInt(newValue));
                else settings.setPillow1PressureInterval(1);
            }
        });

        button2InflateEditText.addTextChangedListener(new TextChangedListener<EditText>(button2InflateEditText) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                String newValue = target.getText().toString();
                if (!newValue.isEmpty()) settings.setPillow2PressureInterval(Integer.parseInt(newValue));
                else settings.setPillow2PressureInterval(1);
            }
        });

    }




}
