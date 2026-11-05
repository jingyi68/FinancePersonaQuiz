package my.edu.mckl;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class QuizActivity extends AppCompatActivity {

    // Connects QuizActivity to SQLite database
    private DatabaseHelper db;

    //stores questions retrieved from sqlite
    private ArrayList<String> databaseQuestions = new ArrayList<>();

    //stores the database id for each question
    private ArrayList<Long> questionIds = new ArrayList<>();

    //stores the four ans opt retrieved from sqlite
    private String[] databaseOptions = new String[4];

    //stores the financial persona category for each option
    private String[] optionCategories = new String[4];

    //stores the points awarded by each option
    private int[] optionPoints = new int[4];

    private TextView tvQuestionNumber;
    private TextView tvQuestion;

    private ProgressBar progressBar;

    private Button btnOption1;
    private Button btnOption2;
    private Button btnOption3;
    private Button btnOption4;

    // button for navigating to the previous question
    private Button btnBack;

    // Keeps track of which question the user is answering
    private int currentQuestion = 0;

    //stores the selected answer for each question, -1 means the question has not been answered
    private int[] selectedAnswers;

    // Scores for the four financial personas
    private int saverScore = 0;
    private int spenderScore = 0;
    private int investorScore = 0;
    private int shopperScore = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //Initialize the SQLite database
        db = new DatabaseHelper(this);

        // Connect Java variables to XML views
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestion = findViewById(R.id.tvQuestion);

        progressBar = findViewById(R.id.progressBar);

        btnOption1 = findViewById(R.id.btnOption1);
        btnOption2 = findViewById(R.id.btnOption2);
        btnOption3 = findViewById(R.id.btnOption3);
        btnOption4 = findViewById(R.id.btnOption4);

        // connect the back button to its XML layout
        btnBack = findViewById(R.id.btnBack);

        // Restore the visible question after screen rotation, if needed.
        if (savedInstanceState != null) {
            currentQuestion = savedInstanceState.getInt("currentQuestion", 0);
        }

        loadQuestionsFromDatabase();

        selectedAnswers = new int[databaseQuestions.size()];
        Arrays.fill(selectedAnswers, -1);

        if (databaseQuestions.isEmpty()) {
            tvQuestion.setText("No questions found in database.");
            return;
        }

        // Rebuild all answer selections and scores from SQLite, not just RAM.
        // This also lets a user leave and resume an unfinished quiz.
        try (Cursor saved = db.getSavedProgress()) {
            while (saved.moveToNext()) {
                long questionId = saved.getLong(
                        saved.getColumnIndexOrThrow("question_id"));
                int questionIndex = questionIds.indexOf(questionId);
                int option = saved.getInt(
                        saved.getColumnIndexOrThrow("option_position"));
                if (questionIndex < 0 || option < 0 || option >= 4) {
                    continue;
                }
                selectedAnswers[questionIndex] = option;
                String category = saved.getString(
                        saved.getColumnIndexOrThrow("category_name"));
                int points = saved.getInt(
                        saved.getColumnIndexOrThrow("points"));
                updatePersonaScore(category, points);
            }
        }

        if (savedInstanceState == null) {
            // Return to the first unanswered question on a fresh launch.
            for (int i = 0; i < selectedAnswers.length; i++) {
                if (selectedAnswers[i] == -1) {
                    currentQuestion = i;
                    break;
                }
            }
        }
        currentQuestion = Math.max(0,
                Math.min(currentQuestion, databaseQuestions.size() - 1));

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

        // handle back button clicks
        btnBack.setOnClickListener(v -> goBack());
    }

    // Retrieve all quiz questions from SQLite
    private void loadQuestionsFromDatabase() {

        Cursor cursor = db.getAllQuestions();

        databaseQuestions.clear();

        questionIds.clear();

        try {
            while (cursor.moveToNext()) {

                String question = cursor.getString(
                        cursor.getColumnIndexOrThrow("question_text")
                );

                databaseQuestions.add(question);

                // Get the unique ID of the question
                long questionId = cursor.getLong(
                        cursor.getColumnIndexOrThrow("id")
                );

                // Store the ID for retrieving its answer options later
                questionIds.add(questionId);
            }
        } finally {
            cursor.close();
        }
    }

    // Retrieve answer options for the current question from SQLite
    private void loadOptionsFromDatabase() {

        // Get the database ID of the current question
        long questionId = questionIds.get(currentQuestion);

        // Retrieve options belonging to this question
        Cursor cursor = db.getOptionsForQuestion(questionId);

        try {
            while (cursor.moveToNext()) {

                // Get the position of the option (0 to 3)
                int position = cursor.getInt(
                        cursor.getColumnIndexOrThrow("option_position")
                );

                // Get the answer option text
                String optionText = cursor.getString(
                        cursor.getColumnIndexOrThrow("option_text")
                );

                //retrieve the persona category linked to this option
                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow("category_name")
                );

                // Retrieve the points awarded for selecting this option
                int points = cursor.getInt(
                        cursor.getColumnIndexOrThrow("points")
                );

                // Store the option in the correct position
                if (position >= 0 && position < databaseOptions.length) {

                    databaseOptions[position] = optionText;

                    // Store the category and points for this option
                    optionCategories[position] = category;
                    optionPoints[position] = points;
                }
            }
        } finally {
            cursor.close();
        }
    }


    private void displayQuestion() {

        // Update question number
        tvQuestionNumber.setText(
                "Question " + (currentQuestion + 1) + " of " + databaseQuestions.size()
        );

        // Update progress bar
        progressBar.setProgress(currentQuestion + 1);

        // Display question
        tvQuestion.setText(databaseQuestions.get(currentQuestion));

        //Load answer options for the current question from SQLite;
        loadOptionsFromDatabase();

        // Display the four options
        btnOption1.setText(databaseOptions[0]);
        btnOption2.setText(databaseOptions[1]);
        btnOption3.setText(databaseOptions[2]);
        btnOption4.setText(databaseOptions[3]);


        // Group the four answer buttons together
        Button[] answerButtons = {
                btnOption1,
                btnOption2,
                btnOption3,
                btnOption4
        };

        // Find the previously selected answer
        int previousAnswer = selectedAnswers[currentQuestion];

        // Mark the selected answer with a checkmark
        if (previousAnswer >= 0 && previousAnswer < answerButtons.length) {

            answerButtons[previousAnswer].setText(
                    "✓ " + databaseOptions[previousAnswer]
            );
        }

    }


    // Navigate to the previous question or home screen
    private void goBack() {

        // Check whether the user is on the first question
        if (currentQuestion > 0) {

            // Move to the previous question
            currentQuestion--;

            // Display the previous question and its options
            displayQuestion();

        } else {

            // Return to the home screen
            finish();
        }
    }

    private void selectAnswer(int selectedOption) {

        // Persist the answer and its points before changing the screen.
        // A second selection for the same question replaces the old row.
        try {
            if (!db.saveProgressAnswer(questionIds.get(currentQuestion), selectedOption)) {
                Toast.makeText(this, "Could not save your answer", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (RuntimeException e) {
            android.util.Log.e("FinancePersonaQuiz", "Saving answer failed", e);
            Toast.makeText(this, "Database error: answer not saved", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check whether this question was answered previously
        int previousAnswer = selectedAnswers[currentQuestion];

        // Remove the previous answer's points if necessary
        if (previousAnswer != -1) {
            String oldCategory = optionCategories[previousAnswer];
            int oldPoints = optionPoints[previousAnswer];

            updatePersonaScore(oldCategory, -oldPoints);
        }

        // Retrieve the new answer's category and points
        String newCategory = optionCategories[selectedOption];
        int newPoints = optionPoints[selectedOption];

        // Add the new answer's points
        updatePersonaScore(newCategory, newPoints);

        // Save the latest selected answer
        selectedAnswers[currentQuestion] = selectedOption;

        // Move to the next question
        if (currentQuestion < databaseQuestions.size() - 1) {

            currentQuestion++;
            displayQuestion();

        } else {

            // All questions have been answered
            showResult();
        }
    }

    // Adds or removes points from the correct financial persona
    private void updatePersonaScore(String category, int points) {

        switch (category) {

            case "The Super Saver":
                saverScore += points;
                break;

            case "The Experiential Spender":
                spenderScore += points;
                break;

            case "The Savvy Investor":
                investorScore += points;
                break;

            case "The Carefree Shopper":
                shopperScore += points;
                break;

            default:
                throw new IllegalStateException(
                        "Unknown financial persona: " + category
                );
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

        // Save the completed quiz result into SQLite
        long resultId = db.saveQuizResult(
                result,
                saverScore,
                spenderScore,
                investorScore,
                shopperScore
        );

        // Check whether the result was saved successfully
        if (resultId == -1) {
            Toast.makeText(
                    this,
                    "Failed to save quiz result",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Clear unfinished answers only after the final result is saved.
        db.clearProgress();

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

        // Scores and selected answers are restored from SQLite on recreation.
    }

}