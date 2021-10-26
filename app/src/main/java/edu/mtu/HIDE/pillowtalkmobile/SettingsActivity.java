package edu.mtu.HIDE.pillowtalkmobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText pillow1NicknameEditText;
    private EditText pillow2NicknameEditText;
    private EditText pillow1LowInflateEditText;
    private EditText pillow2LowInflateEditText;
    private EditText pillow1MediumInflateEditText;
    private EditText pillow2MediumInflateEditText;
    private EditText pillow1HighInflateEditText;
    private EditText pillow2HighInflateEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Intent intent = getIntent();
        final UserSettings settings = new UserSettings(this);

        pillow1NicknameEditText = findViewById(R.id.pillow_1_nickname_text);
        pillow2NicknameEditText = findViewById(R.id.pillow_2_nickname_text);

        pillow1LowInflateEditText = findViewById(R.id.pillow_1_pressure_interval_low_text);
        pillow2LowInflateEditText = findViewById(R.id.pillow_2_pressure_interval_low_text);
        pillow1MediumInflateEditText = findViewById(R.id.pillow_1_pressure_interval_medium_text);
        pillow2MediumInflateEditText = findViewById(R.id.pillow_2_pressure_interval_medium_text);
        pillow1HighInflateEditText = findViewById(R.id.pillow_1_pressure_interval_high_text);
        pillow2HighInflateEditText = findViewById(R.id.pillow_2_pressure_interval_high_text);

        pillow1NicknameEditText.setText(settings.getPillow1Nickname());
        pillow2NicknameEditText.setText(settings.getPillow2Nickname());

        pillow1LowInflateEditText.setText(String.valueOf(settings.getPillow1LowPressureInterval()));
        pillow2LowInflateEditText.setText(String.valueOf(settings.getPillow2LowPressureInterval()));
        pillow1MediumInflateEditText.setText(String.valueOf(settings.getPillow1MediumPressureInterval()));
        pillow2MediumInflateEditText.setText(String.valueOf(settings.getPillow2MediumPressureInterval()));
        pillow1HighInflateEditText.setText(String.valueOf(settings.getPillow1HighPressureInterval()));
        pillow2HighInflateEditText.setText(String.valueOf(settings.getPillow2HighPressureInterval()));

        pillow1NicknameEditText.setText(settings.getPillow1Nickname());
        pillow2NicknameEditText.setText(settings.getPillow2Nickname());

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
    }
}