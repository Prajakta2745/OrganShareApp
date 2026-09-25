package com.example.organshare.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.example.organshare.utils.TextColorManager;
import com.example.organshare.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;

public class SettingsActivity extends AppCompatActivity {

    private TextView settingEditProfile, settingChangePassword, settingAboutApp, settingPrivacyPolicy;
    private RadioGroup rgThemeMode;
    private RadioButton rbThemeLight, rbThemeDark, rbThemeSystem;
    private RadioButton rbColorDefault, rbColorBlue, rbColorGreen, rbColorPurple;
    private MaterialButton btnSettingsLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
        loadCurrentPreferences();
        setupListeners();
    }

    private void initViews() {
        settingEditProfile = findViewById(R.id.settingEditProfile);
        settingChangePassword = findViewById(R.id.settingChangePassword);
        settingAboutApp = findViewById(R.id.settingAboutApp);
        settingPrivacyPolicy = findViewById(R.id.settingPrivacyPolicy);
        rgThemeMode = findViewById(R.id.rgThemeMode);
        rbThemeLight = findViewById(R.id.rbThemeLight);
        rbThemeDark = findViewById(R.id.rbThemeDark);
        rbThemeSystem = findViewById(R.id.rbThemeSystem);

        rbColorDefault = findViewById(R.id.rbColorDefault);
        rbColorBlue = findViewById(R.id.rbColorBlue);
        rbColorGreen = findViewById(R.id.rbColorGreen);
        rbColorPurple = findViewById(R.id.rbColorPurple);

        btnSettingsLogout = findViewById(R.id.btnSettingsLogout);
    }

    private void loadCurrentPreferences() {
        int theme = ThemeManager.getThemeMode(this);
        if (theme == ThemeManager.THEME_LIGHT) rbThemeLight.setChecked(true);
        else if (theme == ThemeManager.THEME_DARK) rbThemeDark.setChecked(true);
        else rbThemeSystem.setChecked(true);

        String colorPref = TextColorManager.getTextColorPreference(this);
        if (TextColorManager.COLOR_BLUE.equalsIgnoreCase(colorPref)) rbColorBlue.setChecked(true);
        else if (TextColorManager.COLOR_GREEN.equalsIgnoreCase(colorPref)) rbColorGreen.setChecked(true);
        else if (TextColorManager.COLOR_PURPLE.equalsIgnoreCase(colorPref)) rbColorPurple.setChecked(true);
        else rbColorDefault.setChecked(true);
    }

    private void setupListeners() {
        settingEditProfile.setOnClickListener(v -> startActivity(new Intent(this, EditProfileActivity.class)));

        settingChangePassword.setOnClickListener(v -> startActivity(new Intent(this, ChangePasswordActivity.class)));

        settingAboutApp.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("About OrganShare")
                    .setMessage("OrganShare v1.0\nOrgan Donation & Allocation Logistics Platform.\nBuilt with native Java + XML layouts and Firebase integration.")
                    .setPositiveButton("OK", null)
                    .show();
        });

        settingPrivacyPolicy.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Privacy & Safeguards")
                    .setMessage("OrganShare strictly enforces role-based access control. Sensitive personal and emergency donor details are masked. All matching recommendations require independent clinical evaluation by licensed physicians.")
                    .setPositiveButton("Understood", null)
                    .show();
        });

        rgThemeMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbThemeLight) {
                ThemeManager.setThemeMode(this, ThemeManager.THEME_LIGHT);
            } else if (checkedId == R.id.rbThemeDark) {
                ThemeManager.setThemeMode(this, ThemeManager.THEME_DARK);
            } else {
                ThemeManager.setThemeMode(this, ThemeManager.THEME_SYSTEM);
            }
            Toast.makeText(this, "Theme setting updated", Toast.LENGTH_SHORT).show();
        });

        rbColorDefault.setOnClickListener(v -> saveTextColor(TextColorManager.COLOR_DEFAULT));
        rbColorBlue.setOnClickListener(v -> saveTextColor(TextColorManager.COLOR_BLUE));
        rbColorGreen.setOnClickListener(v -> saveTextColor(TextColorManager.COLOR_GREEN));
        rbColorPurple.setOnClickListener(v -> saveTextColor(TextColorManager.COLOR_PURPLE));

        btnSettingsLogout.setOnClickListener(v -> NavigationDrawerHelper.showLogoutDialog(this));
    }

    private void saveTextColor(String colorCode) {
        TextColorManager.setTextColorPreference(this, colorCode);
        Toast.makeText(this, "Accent color preference saved", Toast.LENGTH_SHORT).show();
    }
}
