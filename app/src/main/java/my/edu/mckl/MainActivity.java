package my.edu.mckl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private TextView tvLastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Connect Java variables to XML views
        btnStart = findViewById(R.id.btnStart);
        tvLastResult = findViewById(R.id.tvLastResult);

        // Open QuizActivity when Start Quiz is clicked
        btnStart.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    QuizActivity.class
            );

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Access saved quiz information
        SharedPreferences preferences =
                getSharedPreferences("FinanceQuizPrefs", MODE_PRIVATE);

        // Retrieve the previous persona and quiz date
        String lastPersona =
                preferences.getString("lastPersona", null);

        String lastDate =
                preferences.getString("lastDate", "");

        // Display previous result if available
        if (lastPersona != null) {

            tvLastResult.setText(
                    "Your Last Result\n\n" +
                            lastPersona +
                            "\n" +
                            lastDate
            );

        } else {

            tvLastResult.setText(
                    "No previous quiz taken yet"
            );
        }
    }
}