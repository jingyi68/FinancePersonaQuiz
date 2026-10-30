package my.edu.mckl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvPersonaIcon;
    private TextView tvPersonaName;
    private TextView tvDescription;
    private TextView tvActionSteps;

    private Button btnStartOver;

    private ScrollView resultBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Connect Java variables to XML views
        tvPersonaIcon = findViewById(R.id.tvPersonaIcon);
        tvPersonaName = findViewById(R.id.tvPersonaName);
        tvDescription = findViewById(R.id.tvDescription);
        tvActionSteps = findViewById(R.id.tvActionSteps);

        btnStartOver = findViewById(R.id.btnStartOver);

        resultBackground = findViewById(R.id.resultBackground);

        // Receive the calculated persona from QuizActivity
        String persona = getIntent().getStringExtra("persona");

        // Prevent errors if no persona is received
        if (persona == null) {
            persona = "Unknown Persona";
        }

        // Display the matched persona name
        tvPersonaName.setText(persona);

        String description;
        String actionSteps;

        // Default result screen colours
        String backgroundColor = "#F5F7FA";
        String accentColor = "#2563EB";

        // Select the appropriate description and financial advice
        switch (persona) {

            case "The Super Saver":

                tvPersonaIcon.setText("💰");

                backgroundColor = "#FFF8E7";
                accentColor = "#A16207";

                description =
                        "You prioritise financial security and carefully manage " +
                                "your spending. You prefer predictable financial decisions " +
                                "and feel most comfortable when you have savings available " +
                                "for the future.";

                actionSteps =
                        "• Maintain an emergency fund\n\n" +
                                "• Explore high-yield savings options\n\n" +
                                "• Consider diversified investments\n\n" +
                                "• Create a small fun money budget";

                break;


            case "The Experiential Spender":

                tvPersonaIcon.setText("✈️");

                backgroundColor = "#F6EDFF";
                accentColor = "#7E22CE";

                description =
                        "You see money as a way to enjoy life and create memorable " +
                                "experiences. While you value happiness in the present, " +
                                "balancing experiences with future financial goals can " +
                                "help you maintain financial stability.";

                actionSteps =
                        "• Create a dedicated travel or experience budget\n\n" +
                                "• Automate monthly savings\n\n" +
                                "• Set spending limits for entertainment\n\n" +
                                "• Track discretionary expenses";

                break;


            case "The Savvy Investor":

                tvPersonaIcon.setText("📈");

                backgroundColor = "#E7F6EE";
                accentColor = "#166534";

                description =
                        "You are focused on using money strategically to build " +
                                "future wealth. You are comfortable taking calculated " +
                                "risks and tend to analyse financial opportunities " +
                                "before making decisions.";

                actionSteps =
                        "• Research diversified investment options\n\n" +
                                "• Maintain an emergency fund\n\n" +
                                "• Set long-term investment goals\n\n" +
                                "• Review your portfolio regularly";

                break;


            case "The Carefree Shopper":

                tvPersonaIcon.setText("🛍️");

                backgroundColor = "#FFF0F5";
                accentColor = "#BE185D";

                description =
                        "You tend to approach money casually and may prioritise " +
                                "immediate purchases over long-term planning. Building " +
                                "simple financial habits could help you gain more control " +
                                "without making money management feel restrictive.";

                actionSteps =
                        "• Begin tracking monthly expenses\n\n" +
                                "• Create a simple spending budget\n\n" +
                                "• Enable automatic savings\n\n" +
                                "• Set limits for impulse purchases";

                break;


            default:

                tvPersonaIcon.setText("💰");

                description = "Unable to determine your financial persona.";

                actionSteps = "Please restart the quiz and try again.";

                break;
        }

        // Display the description and action steps
        tvDescription.setText(description);
        tvActionSteps.setText(actionSteps);

        // Change the result screen background
        resultBackground.setBackgroundColor(
                Color.parseColor(backgroundColor)
        );

        // Change the persona name colour
        tvPersonaName.setTextColor(
                Color.parseColor(accentColor)
        );

        // Change the Start Over button colour
        btnStartOver.setBackgroundTintList(
                ColorStateList.valueOf(
                        Color.parseColor(accentColor)
                )
        );


        // Start Over button
        btnStartOver.setOnClickListener(v -> {

            // Create a new quiz session
            Intent intent = new Intent(
                    ResultActivity.this,
                    QuizActivity.class
            );

            startActivity(intent);

            // Close the current result screen
            finish();
        });

    }
}