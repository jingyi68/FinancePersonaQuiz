package my.edu.mckl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite database for the Finance Persona Quiz.
 * Member 1 owns this class; Member 2 can call its public methods from Activities.
 * The seed questions and options match Assignment 1 exactly.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "finance_persona_quiz.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_QUESTIONS = "questions";
    public static final String TABLE_OPTIONS = "answer_options";
    public static final String TABLE_RESULTS = "quiz_results";

    public DatabaseHelper(Context context) {
        this(context, DATABASE_NAME);
    }

    // Separate database name makes it safe to run isolated instrumented tests.
    DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE)");

        db.execSQL("CREATE TABLE " + TABLE_QUESTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question_text TEXT NOT NULL, " +
                "sort_order INTEGER NOT NULL UNIQUE)");

        db.execSQL("CREATE TABLE " + TABLE_OPTIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "question_id INTEGER NOT NULL, " +
                "option_position INTEGER NOT NULL, " +
                "option_text TEXT NOT NULL, " +
                "category_id INTEGER NOT NULL, " +
                "points INTEGER NOT NULL DEFAULT 1 CHECK(points >= 0), " +
                "UNIQUE(question_id, option_position), " +
                "FOREIGN KEY(question_id) REFERENCES " + TABLE_QUESTIONS + "(id) ON DELETE CASCADE, " +
                "FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(id))");

        db.execSQL("CREATE TABLE " + TABLE_RESULTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "persona_name TEXT NOT NULL, " +
                "saver_score INTEGER NOT NULL, " +
                "spender_score INTEGER NOT NULL, " +
                "investor_score INTEGER NOT NULL, " +
                "shopper_score INTEGER NOT NULL, " +
                "completed_at INTEGER NOT NULL)");

        seedQuiz(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Appropriate for this class assignment's initial prototype.
        // NOTE: Upgrading after incrementing DATABASE_VERSION erases saved results.
        // Replace with a real migration before releasing a production app.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    private void seedQuiz(SQLiteDatabase db) {
        String[] categoryNames = {
                "The Super Saver", "The Experiential Spender",
                "The Savvy Investor", "The Carefree Shopper"
        };
        long[] categoryIds = new long[4];
        for (int i = 0; i < categoryNames.length; i++) {
            ContentValues row = new ContentValues();
            row.put("name", categoryNames[i]);
            categoryIds[i] = db.insertOrThrow(TABLE_CATEGORIES, null, row);
        }

        String[] questions = {
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

        String[][] answerOptions = {
                { "Save most of it", "Spend it on an experience", "Invest some of it", "Buy something I want" },
                { "Carefully record and budget everything", "Track loosely while still enjoying myself", "Track it to find money I can invest", "I rarely track my spending" },
                { "Ignore it unless I planned to buy it", "Buy it if it would make me happy", "Consider whether it affects my financial goals", "Buy it immediately because it is on sale" },
                { "Build strong savings and financial security", "Have enough money to enjoy life", "Grow my wealth through investments", "I do not really have a financial goal" },
                { "Only go if it fits my budget", "Go because experiences are worth spending on", "Check whether it affects my investment plans", "Say yes and worry about the money later" },
                { "Put money into savings first", "Set aside money for entertainment", "Invest part of it", "Spend normally and see what is left" },
                { "I avoid financial risk whenever possible", "I will take risks for worthwhile experiences", "I am comfortable with calculated risks", "I do not think much about financial risk" },
                { "Keep using my current phone", "Upgrade if it improves my lifestyle", "Compare its financial value before deciding", "Buy the new one because I want it" },
                { "Save first and compare prices carefully", "Think about how much enjoyment I will get", "Analyse whether it is financially worthwhile", "Buy it if I like it enough" },
                { "Financial security comes first", "Money is meant to create experiences", "Money should work and grow for me", "I will worry about money when I need to" }
        };

        for (int i = 0; i < questions.length; i++) {
            ContentValues questionRow = new ContentValues();
            questionRow.put("question_text", questions[i]);
            questionRow.put("sort_order", i);
            long questionId = db.insertOrThrow(TABLE_QUESTIONS, null, questionRow);

            for (int position = 0; position < answerOptions[i].length; position++) {
                ContentValues optionRow = new ContentValues();
                optionRow.put("question_id", questionId);
                optionRow.put("option_position", position);
                optionRow.put("option_text", answerOptions[i][position]);
                optionRow.put("category_id", categoryIds[position]);
                optionRow.put("points", 1); // Assignment 1 awards one point per answer.
                db.insertOrThrow(TABLE_OPTIONS, null, optionRow);
            }
        }
    }

    // CREATE — add a new question and get its generated ID.
    public long addQuestion(String questionText, int sortOrder) {
        ContentValues values = new ContentValues();
        values.put("question_text", questionText);
        values.put("sort_order", sortOrder);
        return getWritableDatabase().insert(TABLE_QUESTIONS, null, values);
    }

    // READ — get questions in the same order as Assignment 1.
    // CALLER MUST CLOSE the Cursor after reading it.
    public Cursor getAllQuestions() {
        return getReadableDatabase().query(
                TABLE_QUESTIONS, new String[]{"id", "question_text", "sort_order"},
                null, null, null, null, "sort_order ASC");
    }

    // READ — get four choices, their linked financial persona, and the points.
    // CALLER MUST CLOSE the Cursor after reading it.
    public Cursor getOptionsForQuestion(long questionId) {
        String sql = "SELECT o.id, o.question_id, o.option_position, " +
                "o.option_text, o.category_id, c.name AS category_name, o.points " +
                "FROM " + TABLE_OPTIONS + " o " +
                "JOIN " + TABLE_CATEGORIES + " c ON o.category_id = c.id " +
                "WHERE o.question_id = ? ORDER BY o.option_position ASC";
        return getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(questionId)});
    }

    // READ — get all four possible persona categories.
    public Cursor getAllCategories() {
        return getReadableDatabase().query(TABLE_CATEGORIES,
                new String[]{"id", "name"}, null, null, null, null, "id ASC");
    }

    // UPDATE — change an existing question's text.
    public int updateQuestion(long questionId, String newText) {
        ContentValues values = new ContentValues();
        values.put("question_text", newText);
        return getWritableDatabase().update(TABLE_QUESTIONS, values,
                "id = ?", new String[]{String.valueOf(questionId)});
    }

    // DELETE — remove a question. Its options will be removed automatically.
    public int deleteQuestion(long questionId) {
        return getWritableDatabase().delete(TABLE_QUESTIONS,
                "id = ?", new String[]{String.valueOf(questionId)});
    }

    // CREATE — add an answer option to a question.
    public long addOption(long questionId, int position, String text,
                          long categoryId, int points) {
        ContentValues values = new ContentValues();
        values.put("question_id", questionId);
        values.put("option_position", position);
        values.put("option_text", text);
        values.put("category_id", categoryId);
        values.put("points", points);
        return getWritableDatabase().insert(TABLE_OPTIONS, null, values);
    }

    // UPDATE — edit the answer text, linked persona, and points.
    public int updateOption(long optionId, String newText,
                            long categoryId, int points) {
        ContentValues values = new ContentValues();
        values.put("option_text", newText);
        values.put("category_id", categoryId);
        values.put("points", points);
        return getWritableDatabase().update(TABLE_OPTIONS, values,
                "id = ?", new String[]{String.valueOf(optionId)});
    }

    // DELETE — remove a single answer option.
    public int deleteOption(long optionId) {
        return getWritableDatabase().delete(TABLE_OPTIONS,
                "id = ?", new String[]{String.valueOf(optionId)});
    }

    // CREATE — record the final persona and all four scores after a quiz.
    public long saveQuizResult(String personaName, int saverScore,
                               int spenderScore, int investorScore,
                               int shopperScore) {
        ContentValues values = new ContentValues();
        values.put("persona_name", personaName);
        values.put("saver_score", saverScore);
        values.put("spender_score", spenderScore);
        values.put("investor_score", investorScore);
        values.put("shopper_score", shopperScore);
        values.put("completed_at", System.currentTimeMillis());
        return getWritableDatabase().insert(TABLE_RESULTS, null, values);
    }

    // READ — latest saved result (zero rows if quiz has never been completed).
    public Cursor getLatestResult() {
        return getReadableDatabase().query(TABLE_RESULTS, null,
                null, null, null, null, "completed_at DESC, id DESC", "1");
    }

    public Cursor getAllResults() {
        return getReadableDatabase().query(TABLE_RESULTS, null,
                null, null, null, null, "completed_at DESC, id DESC");
    }

    // DELETE — remove a saved result by its ID.
    public int deleteResult(long resultId) {
        return getWritableDatabase().delete(TABLE_RESULTS,
                "id = ?", new String[]{String.valueOf(resultId)});
    }

    // Convenient counts for verifying the seeded data with Android Studio.
    public long countQuestions() {
        return android.database.DatabaseUtils.queryNumEntries(
                getReadableDatabase(), TABLE_QUESTIONS);
    }

    public long countOptions() {
        return android.database.DatabaseUtils.queryNumEntries(
                getReadableDatabase(), TABLE_OPTIONS);
    }
}
