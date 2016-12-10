package edu.sjsu.cmpe277.org.mathsquizcmpe277;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MathsQuizActivity extends AppCompatActivity implements QuizHomeScreenFrag.OnFragmentInteractionListener {

    public final static String QUIZ_CATEGORY = "QUIZ_CATEGORY";
    private QuizHomeScreenFrag quizHomeScreenFrag = null;
    private String QUIZ_HOME_SCREEN = "home_screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maths_quiz);

        // for smaller or normal screen size devices only portrait mode
        if (((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) !=
                Configuration.SCREENLAYOUT_SIZE_LARGE) && ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) !=
                Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        this.setTitle(getString(R.string.app_name));

        FragmentManager fragmentManager = getFragmentManager();
        quizHomeScreenFrag = (QuizHomeScreenFrag) fragmentManager.findFragmentByTag(QUIZ_HOME_SCREEN);

        if (quizHomeScreenFrag == null) {
            quizHomeScreenFrag = new QuizHomeScreenFrag();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(quizHomeScreenFrag, QUIZ_HOME_SCREEN);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TEST", "PAUSE");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TEST", "RESUME");
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("MathsQuizActivity", uri.toString());
    }

    // start the quiz
    public void startQuiz(String category) {
        Intent intent = new Intent(this, QuesNAnsActivity.class);
        intent.putExtra(QUIZ_CATEGORY, category);

        startActivity(intent);
    }

    // quit the app
    public void quitApp() {
        finish();
        System.exit(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        quizHomeScreenFrag.resetAll();
        System.gc();
    }
}