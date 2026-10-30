package my.edu.mckl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;


public class QuizActivity extends AppCompatActivity {

    private TextView tvQuestionNumber;
    private TextView tvQuestion;

    private ProgressBar progressBar;

    private Button btnOption1;
    private Button btnOption2;
    private Button btnOption3;
    private Button btnOption4;

    // Keeps track of which question the user is answering
    private int currentQuestion = 0;

    // Scores for the four financial personas
    private int saverScore = 0;
    private int spenderScore = 0;
    private int investorScore = 0;
    private int shopperScore = 0;

    // Quiz questions
    private String[] questions = {

            "You receive RM500 unexpectedly. What do you do?",

            "How do you normally track your monthly spending?",

            "You see a flash sale for something you like. What do you do?",

            "What is your main financial goal?",

            "Your friends invite you on an expensive trip. What do you do?",

            "What do you usually do after receiving your salary?",

            "How do you feel about financial risk?",

            "Your current phone still works, but a new model is released. What do you do?",

            "What do you usually do before making a large purchase?",

            "Which statement sounds most like you?"
    };

    // Four options for every question
    private String[][] options = {

            {
                    "Save most of it",
                    "Spend it on an experience",
                    "Invest some of it",
                    "Buy something I want"
            },

            {
                    "Carefully record and budget everything",
                    "Track loosely while still enjoying myself",
                    "Track it to find money I can invest",
                    "I rarely track my spending"
            },

            {
                    "Ignore it unless I planned to buy it",
                    "Buy it if it would make me happy",
                    "Consider whether it affects my financial goals",
                    "Buy it immediately because it is on sale"
            },

            {
                    "Build strong savings and financial security",
                    "Have enough money to enjoy life",
                    "Grow my wealth through investments",
                    "I do not really have a financial goal"
            },

            {
                    "Only go if it fits my budget",
                    "Go because experiences are worth spending on",
                    "Check whether it affects my investment plans",
                    "Say yes and worry about the money later"
            },

            {
                    "Put money into savings first",
                    "Set aside money for entertainment",
                    "Invest part of it",
                    "Spend normally and see what is left"
            },

            {
                    "I avoid financial risk whenever possible",
                    "I will take risks for worthwhile experiences",
                    "I am comfortable with calculated risks",
                    "I do not think much about financial risk"
            },

            {
                    "Keep using my current phone",
                    "Upgrade if it improves my lifestyle",
                    "Compare its financial value before deciding",
                    "Buy the new one because I want it"
            },

            {
                    "Save first and compare prices carefully",
                    "Think about how much enjoyment I will get",
                    "Analyse whether it is financially worthwhile",
                    "Buy it if I like it enough"
            },

            {
                    "Financial security comes first",
                    "Money is meant to create experiences",
                    "Money should work and grow for me",
                    "I will worry about money when I need to"
            }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Connect Java variables to XML views
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);

        progressBar = findViewById(R.id.progressBar);

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);


        // Restore quiz progress if the Activity was recreated
        if (savedInstanceState != null) {

            currentQuestion =
                    savedInstanceState.getInt("currentQuestion", 0);

            saverScore =
                    savedInstanceState.getInt("saverScore", 0);

            spenderScore =
                    savedInstanceState.getInt("spenderScore", 0);

            investorScore =
                    savedInstanceState.getInt("investorScore", 0);

            shopperScore =
                    savedInstanceState.getInt("shopperScore", 0);
        }

        // Display the current question
        displayQuestion();


        // Option 1 - Super Saver
        btnOption1.setOnClickListener(v -> selectAnswer(0));

        // Option 2 - Experiential Spender
        btnOption2.setOnClickListener(v -> selectAnswer(1));

        // Option 3 - Savvy Investor
        btnOption3.setOnClickListener(v -> selectAnswer(2));

        // Option 4 - Carefree Shopper
        btnOption4.setOnClickListener(v -> selectAnswer(3));
    }


    private void displayQuestion() {

        // Update question number
        tvQuestionNumber.setText(
                "Question " + (currentQuestion + 1) + " of " + questions.length
        );

        // Update progress bar
        progressBar.setProgress(currentQuestion + 1);

        // Display question
        tvQuestion.setText(questions[currentQuestion]);

        // Display the four options
        btnOption1.setText(options[currentQuestion][0]);
        btnOption2.setText(options[currentQuestion][1]);
        btnOption3.setText(options[currentQuestion][2]);
        btnOption4.setText(options[currentQuestion][3]);
    }


    private void selectAnswer(int selectedOption) {

        // Increase the score based on the selected answer
        switch (selectedOption) {

            case 0:
                saverScore++;
                break;

            case 1:
                spenderScore++;
                break;

            case 2:
                investorScore++;
                break;

            case 3:
                shopperScore++;
                break;
        }

        // Check whether there are more questions
        if (currentQuestion < questions.length - 1) {

            // Move to the next question
            currentQuestion++;

            // Update the screen
            displayQuestion();

        } else {

            // All 10 questions have been answered
            showResult();
        }
    }


    private String calculatePersona() {

        if (saverScore >= spenderScore &&
                saverScore >= investorScore &&
                saverScore >= shopperScore) {

            return "The Super Saver";

        } else if (spenderScore >= investorScore &&
                spenderScore >= shopperScore) {

            return "The Experiential Spender";

        } else if (investorScore >= shopperScore) {

            return "The Savvy Investor";

        } else {

            return "The Carefree Shopper";
        }
    }


    private void showResult() {

        // Calculate the winning financial persona
        String result = calculatePersona();

        // Get the current date
        String date = new SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
        ).format(new Date());

        // Open SharedPreferences
        SharedPreferences preferences =
                getSharedPreferences("FinanceQuizPrefs", MODE_PRIVATE);

        // Save the matched persona and completion date
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("lastPersona", result);
        editor.putString("lastDate", date);

        editor.apply();

        // Open ResultActivity
        Intent intent = new Intent(
                QuizActivity.this,
                ResultActivity.class
        );

        // Send the winning persona
        intent.putExtra("persona", result);

        startActivity(intent);

        // Close the completed quiz
        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        // Save the current question
        outState.putInt("currentQuestion", currentQuestion);

        // Save all four persona scores
        outState.putInt("saverScore", saverScore);
        outState.putInt("spenderScore", spenderScore);
        outState.putInt("investorScore", investorScore);
        outState.putInt("shopperScore", shopperScore);
    }

}