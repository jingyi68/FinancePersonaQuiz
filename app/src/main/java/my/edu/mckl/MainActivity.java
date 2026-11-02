package my.edu.mckl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.database.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStart;
    private TextView tvLastResult;

    //database helper for retrieving saved quiz results
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize sqlite database helper
        db = new DatabaseHelper(this);

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

        // Retrieve the latest completed quiz from SQLite
        Cursor cursor = db.getLatestResult();

        try {
            if (cursor.moveToFirst()) {

                // Retrieve the previous financial persona
                String lastPersona = cursor.getString(
                        cursor.getColumnIndexOrThrow("persona_name")
                );

                // Retrieve the completion timestamp
                long timestamp = cursor.getLong(
                        cursor.getColumnIndexOrThrow("completed_at")
                );

                // Convert the timestamp into a readable date
                String lastDate = new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                ).format(new Date(timestamp));

                // Display the previous quiz result
                tvLastResult.setText(
                        "Your Last Result\n\n" +
                                lastPersona +
                                "\n" +
                                lastDate
                );

            } else {

                // Display a message if no results exist
                tvLastResult.setText(
                        "No previous quiz taken yet"
                );
            }
        } finally {
            cursor.close();
        }
    }
}