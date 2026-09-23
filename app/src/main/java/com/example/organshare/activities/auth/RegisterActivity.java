package com.example.organshare.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.auth.RoleRouter;
import com.example.organshare.models.DeliveryPersonnelProfile;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.OrganBankProfile;
import com.example.organshare.models.UserModel;
import com.example.organshare.repositories.AuthRepository;
import com.example.organshare.repositories.DeliveryRepository;
import com.example.organshare.repositories.DonorRepository;
import com.example.organshare.repositories.HospitalRepository;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.IdGenerator;
import com.example.organshare.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity {

    private AutoCompleteTextView actvRole, actvBloodGroup, actvGender;
    private TextInputEditText etDisplayName, etEmail, etPhone, etCity, etEmergencyContact, etPassword, etConfirmPassword;
    private CheckBox cbConsent;
    private LinearLayout layoutDonorFields;
    private MaterialButton btnRegister;
    private ProgressBar progressBar;
    private TextView tvLogin;
    private AuthManager authManager;

    private final String[] roles = {"Donor", "Hospital", "Organ Bank / Coordinator", "Delivery Personnel", "Administrator"};
    private final String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
    private final String[] genders = {"Male", "Female", "Other", "Prefer not to say"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = AuthManager.getInstance(this);

        initViews();
        setupDropdowns();
        setupListeners();
    }

    private void initViews() {
        actvRole = findViewById(R.id.actvRole);
        actvBloodGroup = findViewById(R.id.actvBloodGroup);
        actvGender = findViewById(R.id.actvGender);
        etDisplayName = findViewById(R.id.etDisplayName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbConsent = findViewById(R.id.cbConsent);
        layoutDonorFields = findViewById(R.id.layoutDonorFields);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupDropdowns() {
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        actvRole.setAdapter(roleAdapter);

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups);
        actvBloodGroup.setAdapter(bloodAdapter);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, genders);
        actvGender.setAdapter(genderAdapter);

        actvRole.setOnItemClickListener((parent, view, position, id) -> {
            String selected = roles[position];
            if ("Donor".equalsIgnoreCase(selected)) {
                layoutDonorFields.setVisibility(View.VISIBLE);
            } else {
                layoutDonorFields.setVisibility(View.GONE);
            }
        });
    }

    private void setupListeners() {
        tvLogin.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> performRegistration());
    }

    private void performRegistration() {
        String displayName = etDisplayName.getText() != null ? etDisplayName.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";
        String selectedRoleText = actvRole.getText().toString().trim();

        String role = Constants.ROLE_DONOR;
        if (selectedRoleText.contains("Hospital")) role = Constants.ROLE_HOSPITAL;
        else if (selectedRoleText.contains("Organ Bank")) role = Constants.ROLE_ORGAN_BANK;
        else if (selectedRoleText.contains("Delivery")) role = Constants.ROLE_DELIVERY;
        else if (selectedRoleText.contains("Admin")) role = Constants.ROLE_ADMIN;

        if (!ValidationUtils.isNotEmpty(displayName)) {
            etDisplayName.setError("Name / Institution is required");
            etDisplayName.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Valid email required");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            etPhone.setError("Valid contact phone required");
            etPhone.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        if (Constants.ROLE_DONOR.equals(role) && !cbConsent.isChecked()) {
            Toast.makeText(this, "Please accept organ donation pledge consent terms to register", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        UserModel user = new UserModel("", email, displayName, role, phone);
        final String finalRole = role;

        authManager.getAuthRepository().registerUser(email, password, user, new AuthRepository.AuthCallback<UserModel>() {
            @Override
            public void onSuccess(UserModel registeredUser) {
                // Initialize specific profile model based on role
                createRoleSpecificProfile(registeredUser, finalRole);
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void createRoleSpecificProfile(UserModel user, String role) {
        if (Constants.ROLE_DONOR.equals(role)) {
            DonorProfile donor = new DonorProfile();
            donor.setDonorId(IdGenerator.generateDonorId());
            donor.setUserId(user.getUid());
            donor.setFirstName(user.getDisplayName());
            donor.setEmail(user.getEmail());
            donor.setPhone(user.getPhone());
            donor.setBloodGroup(actvBloodGroup.getText().toString());
            donor.setGender(actvGender.getText().toString());
            donor.setCity(etCity.getText() != null ? etCity.getText().toString().trim() : "Metro City");
            donor.setGeneralLocation("Central Zone");
            donor.setEmergencyContact(etEmergencyContact.getText() != null ? etEmergencyContact.getText().toString().trim() : "N/A");
            donor.setOrgansWillingToDonate(Arrays.asList("Kidney", "Liver", "Cornea", "Heart"));
            donor.setDonationStatus("PLEDGED");
            donor.setConsentGiven(true);
            donor.setVerified(true);

            new DonorRepository().saveDonorProfile(donor, new DonorRepository.DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) { completeSuccess(user); }
                @Override
                public void onFailure(String error) { completeSuccess(user); }
            });
        } else if (Constants.ROLE_HOSPITAL.equals(role)) {
            HospitalProfile hospital = new HospitalProfile();
            hospital.setHospitalId("HOSP-" + user.getUid().substring(0, 5).toUpperCase());
            hospital.setUserId(user.getUid());
            hospital.setHospitalName(user.getDisplayName());
            hospital.setContactNumber(user.getPhone());
            hospital.setEmail(user.getEmail());
            hospital.setCity("Metro City");
            hospital.setVerified(false); // Requires admin approval

            new HospitalRepository().saveHospitalProfile(hospital, new HospitalRepository.DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) { completeSuccess(user); }
                @Override
                public void onFailure(String error) { completeSuccess(user); }
            });
        } else if (Constants.ROLE_ORGAN_BANK.equals(role)) {
            OrganBankProfile bank = new OrganBankProfile();
            bank.setBankId("BANK-" + user.getUid().substring(0, 5).toUpperCase());
            bank.setUserId(user.getUid());
            bank.setBankName(user.getDisplayName());
            bank.setContactPhone(user.getPhone());
            bank.setEmail(user.getEmail());
            bank.setVerified(false); // Requires admin approval

            new OrganBankRepository().saveBankProfile(bank, new OrganBankRepository.DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) { completeSuccess(user); }
                @Override
                public void onFailure(String error) { completeSuccess(user); }
            });
        } else if (Constants.ROLE_DELIVERY.equals(role)) {
            DeliveryPersonnelProfile courier = new DeliveryPersonnelProfile();
            courier.setDeliveryPersonId("DEL-" + user.getUid().substring(0, 5).toUpperCase());
            courier.setUserId(user.getUid());
            courier.setFullName(user.getDisplayName());
            courier.setPhone(user.getPhone());
            courier.setEmail(user.getEmail());
            courier.setCurrentStatus("AVAILABLE");
            courier.setVerified(true);

            new DeliveryRepository().saveDeliveryPersonnelProfile(courier, new DeliveryRepository.DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) { completeSuccess(user); }
                @Override
                public void onFailure(String error) { completeSuccess(user); }
            });
        } else {
            completeSuccess(user);
        }
    }

    private void completeSuccess(UserModel user) {
        progressBar.setVisibility(View.GONE);
        btnRegister.setEnabled(true);

        authManager.getSessionManager().createLoginSession(
                user.getUid(),
                user.getEmail(),
                user.getDisplayName(),
                user.getRole()
        );

        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
        RoleRouter.navigateToDashboard(this, user.getRole());
    }
}
