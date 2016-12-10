package edu.sjsu.cmpe277.org.mathsquizcmpe277;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class QuesNAnsActivity extends AppCompatActivity implements QuesNAnsFrag.OnFragmentInteractionListener, CustomDialogCallbackListener {

    private String category = null;
    private String QUIZ_SCREEN = "quiz_screen";
    private QuesNAnsFrag quizFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        category = intent.getStringExtra(MathsQuizActivity.QUIZ_CATEGORY);

        setContentView(R.layout.activity_ques_nans);

        // for smaller or normal screen size devices only portrait mode
        if (((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) !=
                Configuration.SCREENLAYOUT_SIZE_LARGE) && ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) !=
                Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        FragmentManager fragmentManager = getFragmentManager();
        quizFragment =  (QuesNAnsFrag) fragmentManager.findFragmentByTag(QUIZ_SCREEN);

        if (quizFragment == null) {
            quizFragment = new QuesNAnsFrag();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(quizFragment, QUIZ_SCREEN);
            fragmentTransaction.commit();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
        savedInstanceState.putBoolean("custom_dialog", CustomDialog.isDialogVisible);
        savedInstanceState.putInt("alert_type", CustomDialog.alertType);

        savedInstanceState.putInt("OP1", QuesNAnsFrag.quesNAnsFrag.op1);
        savedInstanceState.putInt("OP2", QuesNAnsFrag.quesNAnsFrag.op2);
        savedInstanceState.putString("OP", QuesNAnsFrag.quesNAnsFrag.operation);
        savedInstanceState.putInt("currQuesCount", QuesNAnsFrag.quesNAnsFrag.currQuesCount);
        savedInstanceState.putInt("numRightAns", QuesNAnsFrag.quesNAnsFrag.numRightAns);

        boolean timeForNextQues = QuesNAnsFrag.quesNAnsFrag.answered || QuesNAnsFrag.quesNAnsFrag.timeOut;
        savedInstanceState.putBoolean("isAnswered", timeForNextQues);
        if (!timeForNextQues && TimerView.timerView.startTimeMillis != -1) {
            savedInstanceState.putInt("time_diff", (int)(System.currentTimeMillis() - TimerView.timerView.startTimeMillis));
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        CustomDialog.isDialogVisible = savedInstanceState.getBoolean("custom_dialog");
        CustomDialog.alertType = savedInstanceState.getInt("alert_type");

        QuesNAnsFrag.quesNAnsFrag.op1 = savedInstanceState.getInt("OP1");
        QuesNAnsFrag.quesNAnsFrag.op2 = savedInstanceState.getInt("OP2");
        QuesNAnsFrag.quesNAnsFrag.operation = savedInstanceState.getString("OP");
        QuesNAnsFrag.quesNAnsFrag.currQuesCount = savedInstanceState.getInt("currQuesCount");
        QuesNAnsFrag.quesNAnsFrag.numRightAns = savedInstanceState.getInt("numRightAns");

        QuesNAnsFrag.quesNAnsFrag.timeOut = QuesNAnsFrag.quesNAnsFrag.answered = savedInstanceState.getBoolean("isAnswered");
        if (!QuesNAnsFrag.quesNAnsFrag.answered) {
            QuesNAnsFrag.quesNAnsFrag.timeDiff = savedInstanceState.getInt("time_diff");
        }

        if (CustomDialog.isDialogVisible) {
            switch (CustomDialog.alertType) {
                case CustomDialogFragment.ALERT_TYPE_ALERT: {
                    CustomDialog.showAlert(QuesNAnsFrag.quesNAnsFrag, getFragmentManager(), "SCORE CARD", "YOUR SCORE IS " + QuesNAnsFrag.quesNAnsFrag.numRightAns);
                }
                break;

                case CustomDialogFragment.ALERT_TYPE_CONFIRMATION: {
                    CustomDialog.showDialog(this, getFragmentManager(), "CONFIRMATION", "Do you really want to quit Quiz?");
                }
                break;
            }
        }

        QuesNAnsFrag.quesNAnsFrag.setQuesNAns();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override public void onBackPressed()
    {
        CustomDialog.showDialog(this, getFragmentManager(), "CONFIRMATION", "Do you really want to quit Quiz?");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                CustomDialog.showDialog(this, getFragmentManager(), "CONFIRMATION", "Do you really want to quit Quiz?");

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getCategory() {
        return category;
    }

    public void killActivity() {
        finish();
    }

    @Override
    public void onDialogCallback() {
        killActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // release TimerView
        if (TimerView.timerView != null) {
            TimerView.timerView.releaseAll();
            TimerView.timerView.setEnabled(false);
            TimerView.timerView.setActivated(false);
            TimerView.timerView.removeCallbacks(TimerView.timerView);
            TimerView.timerView.destroyDrawingCache();
            TimerView.timerView.clearFocus();
        }

        if (QuesNAnsFrag.quesNAnsFrag != null) {
            QuesNAnsFrag.quesNAnsFrag.resetAll();
        }

        System.gc();
    }
}