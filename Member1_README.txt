CSC1214 Finance Persona Quiz - Member 1 SQLite handoff
====================================================
This package keeps the Assignment 1 screens and their code UNCHANGED.
Two files have been added:
  app/src/main/java/my/edu/mckl/DatabaseHelper.java
  app/src/androidTest/java/my/edu/mckl/DatabaseHelperTest.java

Database tables:
  categories: 4 financial personas
  questions: 10 existing quiz questions
  answer_options: 40 existing options, 1 point each, linked to a category
  quiz_results: completed persona, four score counts, and completion timestamp

IMPORTANT: The interface will continue using the original hardcoded arrays and
SharedPreferences until Member 2 integrates database methods. This is intentional.

How Member 1 can test:
  1. Open the project in Android Studio and let Gradle sync.
  2. Run DatabaseHelperTest under app/src/androidTest on an emulator or phone.
  3. It tests seed data, reading, CRUD operations, saved result, and cascading deletion.
  4. Expected: 1 test passed. Note: test uses a separate finance_quiz_test.db,
     so it will NOT delete the normal app database.

What Member 2 should use:
  DatabaseHelper db = new DatabaseHelper(this);
  db.getAllQuestions() - returns Cursor with id/question_text/sort_order
  db.getOptionsForQuestion(questionId) - returns Cursor with option_text,
     option_position, category_name and points
  db.saveQuizResult(personaName, saver, spender, investor, shopper)
  db.getLatestResult() - Cursor with persona_name and completed_at (Unix millis)

Member 2 must close Cursors (try-with-resources is recommended), preserve quiz
scoring/tie rules, and eventually replace hardcoded question arrays and
SharedPreferences with these SQLite methods. Be sure to close DatabaseHelper
when no longer needed.

NOTE: onUpgrade() demonstrates schema recreation for a class prototype;
if DATABASE_VERSION is increased, saved quiz results will be erased.
For a production app, write a non-destructive migration instead.
