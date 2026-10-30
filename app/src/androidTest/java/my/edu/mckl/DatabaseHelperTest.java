package my.edu.mckl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

/** Place in app/src/androidTest/java/my/edu/mckl/ (not main/java). */
@RunWith(AndroidJUnit4.class)
public class DatabaseHelperTest {

    @Test
    public void databaseSeedsQuestionsAndSupportsCrud() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        String testDatabase = "finance_quiz_test.db";
        context.deleteDatabase(testDatabase);

        try (DatabaseHelper helper = new DatabaseHelper(context, testDatabase)) {
            assertEquals(10L, helper.countQuestions());
            assertEquals(40L, helper.countOptions());

            try (Cursor questions = helper.getAllQuestions()) {
                assertTrue(questions.moveToFirst());
                assertEquals("You receive RM500 unexpectedly. What do you do?",
                        questions.getString(questions.getColumnIndexOrThrow("question_text")));
                long firstQuestionId = questions.getLong(questions.getColumnIndexOrThrow("id"));
                try (Cursor answers = helper.getOptionsForQuestion(firstQuestionId)) {
                    assertEquals(4, answers.getCount());
                    assertTrue(answers.moveToFirst());
                    assertEquals("The Super Saver",
                            answers.getString(answers.getColumnIndexOrThrow("category_name")));
                    assertEquals(1, answers.getInt(answers.getColumnIndexOrThrow("points")));
                }
            }

            long newQuestionId = helper.addQuestion("Temporary test question?", 999);
            assertTrue(newQuestionId > 0L);
            assertEquals(11L, helper.countQuestions());
            long optionId = helper.addOption(newQuestionId, 0, "Test option", 1, 1);
            assertTrue(optionId > 0L);
            assertEquals(41L, helper.countOptions());
            assertEquals(1, helper.updateQuestion(newQuestionId, "Updated test question?"));
            assertEquals(1, helper.updateOption(optionId, "Updated test option", 1, 2));
            assertEquals(1, helper.deleteQuestion(newQuestionId));
            assertEquals(10L, helper.countQuestions());
            assertEquals(40L, helper.countOptions()); // options deleted via ON DELETE CASCADE

            long resultId = helper.saveQuizResult("The Super Saver", 6, 2, 1, 1);
            assertTrue(resultId > 0L);
            try (Cursor result = helper.getLatestResult()) {
                assertTrue(result.moveToFirst());
                assertEquals("The Super Saver",
                        result.getString(result.getColumnIndexOrThrow("persona_name")));
            }
            assertEquals(1, helper.deleteResult(resultId));
        } finally {
            context.deleteDatabase(testDatabase);
        }
    }
}
