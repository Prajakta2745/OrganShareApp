package com.example.organshare.activities.donor;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class EligibilityScreenerActivity extends AppCompatActivity {

    private RadioButton rbAgeAdult, rbChronicNo, rbSurgeryNo, rbBloodIntervalNo;
    private MaterialCardView cardScreenerResult;
    private TextView tvResultTitle, tvResultBody;
    private MaterialButton btnEvaluateScreener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility_screener);

        rbAgeAdult = findViewById(R.id.rbAgeAdult);
        rbChronicNo = findViewById(R.id.rbChronicNo);
        rbSurgeryNo = findViewById(R.id.rbSurgeryNo);
        rbBloodIntervalNo = findViewById(R.id.rbBloodIntervalNo);

        cardScreenerResult = findViewById(R.id.cardScreenerResult);
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvResultBody = findViewById(R.id.tvResultBody);
        btnEvaluateScreener = findViewById(R.id.btnEvaluateScreener);

        btnEvaluateScreener.setOnClickListener(v -> evaluateScreener());
    }

    private void evaluateScreener() {
        boolean isIdealCandidate = rbAgeAdult.isChecked() && rbChronicNo.isChecked() && rbSurgeryNo.isChecked() && rbBloodIntervalNo.isChecked();

        cardScreenerResult.setVisibility(View.VISIBLE);
        if (isIdealCandidate) {
            tvResultTitle.setText("Assessment Result: Potentially Eligible for Evaluation");
            tvResultBody.setText("Based on your answers, you may be eligible for further professional clinical evaluation and blood donation. \n\n"
                    + "Please note: This is a preliminary self-assessment only and not a formal medical diagnosis. Final suitability is confirmed through clinical screening at authorized hospitals or blood banks.");
        } else {
            tvResultTitle.setText("Assessment Result: Evaluation Cooldown / Precautions");
            tvResultBody.setText("Based on your answers, certain temporary restrictions (e.g. recent surgery, active medication, or blood donation cooldown) may apply. \n\n"
                    + "We recommend consulting with a certified transplant coordinator or physician for official clinical clearance.");
        }
    }
}
