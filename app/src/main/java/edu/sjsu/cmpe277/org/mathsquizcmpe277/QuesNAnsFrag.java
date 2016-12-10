package edu.sjsu.cmpe277.org.mathsquizcmpe277;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuesNAnsFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuesNAnsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuesNAnsFrag extends Fragment implements CustomDialogCallbackListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static boolean isViewInitialized = false;

    private String category = null;

    private TextView operand1;
    private TextView operator;
    private TextView operand2;
    private EditText resultField;
    private ImageView resultImage;
    private TextView quesCount;
    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button b5;
    private Button b6;
    private Button b7;
    private Button b8;
    private Button b9;
    private Button b10;
    private Button go;

    private Random random = null;

    private int answer = -1;
    public int currQuesCount = -1;
    public int numRightAns = 0;

    private boolean rightAnswer = false;

    public static QuesNAnsFrag quesNAnsFrag = null;

    public int timeDiff = 0;
    public boolean timeOut = false;
    public boolean answered = false;
    public long startTimeMillis = -1;

    private View animatedTimer = null;
    public QuesNAnsActivity quesActivity = null;

    public int op1 = -1;
    public int op2 = -1;
    public String operation = null;
    public boolean gameUp = false;

    public QuesNAnsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuesNAnsFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static QuesNAnsFrag newInstance(String param1, String param2) {
        QuesNAnsFrag fragment = new QuesNAnsFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ques_n_ans, container, false);

        if (isViewInitialized) {
            return v;
        }
        quesNAnsFrag = this;
        quesActivity = (QuesNAnsActivity) v.getContext();

        isViewInitialized = true;

        // generate random questions
        operand1 = (TextView) v.findViewById(R.id.operand1);
        operand2 = (TextView) v.findViewById(R.id.operand2);
        operator = (TextView) v.findViewById(R.id.operator);
        resultImage = (ImageView) v.findViewById(R.id.resultImage);
        resultField = (EditText) v.findViewById(R.id.resultField);
        animatedTimer = v.findViewById(R.id.animatedTimer);

        quesCount = (TextView) v.findViewById(R.id.quesCount);

        ButtonHandler handler = new ButtonHandler();
        // add number pad
        b1 = (Button) v.findViewById(R.id.b1);
        b1.setOnClickListener(handler);

        b2 = (Button) v.findViewById(R.id.b2);
        b2.setOnClickListener(handler);

        b3 = (Button) v.findViewById(R.id.b3);
        b3.setOnClickListener(handler);

        b4 = (Button) v.findViewById(R.id.b4);
        b4.setOnClickListener(handler);

        b5 = (Button) v.findViewById(R.id.b5);
        b5.setOnClickListener(handler);

        b6 = (Button) v.findViewById(R.id.b6);
        b6.setOnClickListener(handler);

        b7 = (Button) v.findViewById(R.id.b7);
        b7.setOnClickListener(handler);

        b8 = (Button) v.findViewById(R.id.b8);
        b8.setOnClickListener(handler);

        b9 = (Button) v.findViewById(R.id.b9);
        b9.setOnClickListener(handler);

        b10 = (Button) v.findViewById(R.id.b10);
        b10.setOnClickListener(handler);

        go = (Button) v.findViewById(R.id.go);
        go.setOnClickListener(handler);

        category = quesActivity.getCategory();

        random = new Random();

        if (currQuesCount == -1) {
            currQuesCount = 1;

            reset();
            determineQAndAns();
        } else if (timeOut || answered) {

            reset();
            determineQAndAns();
            timeOut = answered = false;
        } else
        {
            answer = op1 + op2;

            operand1.setText(op1 + "");
            operator.setText(operation);
            operand2.setText(op2 + "");

            quesCount.setText(currQuesCount + " / 10");
        }

        return v;
    }

    class ButtonHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.go) {
                checkAnswer();
                TimerView.timerView.answered = answered = true;
            }

            if (answered || timeOut) {
                return;
            }

            resultField.setText(resultField.getText().toString() + ((Button)v).getText());
        }
    }

    public void checkAnswer() {
        String data = resultField.getText().toString();
        if (data == "↵" || data.length() <= 0)
        {
            return;
        }

        int ans = Integer.parseInt(data);
        if (answer == ans) {
            TimerView.timerView.answered = rightAnswer = true;

            return;
        }

        return;
    }

    public void goToNextQuestion() {
        quesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reset();

                if (currQuesCount > 10) {
                    CustomDialog.showAlert(quesNAnsFrag, getFragmentManager(), "SCORE CARD", "YOUR SCORE IS " + numRightAns);

                    gameUp = true;

                    return;
                }

                determineQAndAns();
            }
        });
    }

    private void reset() {
        answer = -1;
        rightAnswer = false;

        quesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                operand1.setText("");
                operand2.setText("");
                operator.setText("");
                quesCount.setText("");

                resultField.setText("");
                resultField.setEnabled(true);

                resultImage.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setQuesNAns() {
        answer = op1 + op2;

        operand1.setText(op1 + "");
        operator.setText(operation);
        operand2.setText(op2 + "");

        quesCount.setText((currQuesCount - 1) + " / 10");
    }

    private void determineQAndAns() {
        quesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // compute question
                switch (category) {
                    case "ADDITION": {
                        op1 = random.nextInt(10);
                        op2 = random.nextInt(10);
                        operation = "+";

                        answer = op1 + op2;
                    }
                    break;

                    case "SUBTRACTION": {
                        op1 = random.nextInt(10);
                        op1 = (op1 == 0) ? 7 : op1;
                        op2 = random.nextInt(op1);
                        operation = "-";

                        answer = op1 - op2;
                    }
                    break;

                    case "MULTIPLICATION": {
                        op1 = random.nextInt(10);
                        op2 = random.nextInt(10);
                        operation = "*";

                        answer = op1 * op2;
                    }
                    break;

                    case "DIV": {
                        op1 = random.nextInt(100);
                        op2 = random.nextInt(op1);
                        op2 = (op2 == 0) ? 7 : op2;
                        operation = "/";

                        float c = (float) op1 / op2;
                    }
                    break;
                }

                operand1.setText(op1 + "");
                operator.setText(operation);
                operand2.setText(op2 + "");

                quesCount.setText(currQuesCount + " / 10");
                currQuesCount++;
            }
        });
    }

    public void showResult() {
        quesActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!rightAnswer)
                {
                    resultImage.setImageResource(R.drawable.quizcross);
                } else {
                    resultImage.setImageResource(R.drawable.quiztick);
                    numRightAns++;
                }
                resultImage.setVisibility(View.VISIBLE);
                resultField.setEnabled(false);
            }
        });
    }

    private void quitQuiz() {
        if (quesActivity == null) {
            return;
        }
        quesActivity.killActivity();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDialogCallback() {
        quitQuiz();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void resetAll() {
        ((ViewGroup)animatedTimer.getParent()).removeView(animatedTimer);
        animatedTimer.setBackgroundDrawable(null);

        operand1.setVisibility(View.GONE);
        operand2.setVisibility(View.GONE);
        operator.setVisibility(View.GONE);
        quesCount.setVisibility(View.GONE);
        resultField.setVisibility(View.GONE);
        resultImage.setVisibility(View.GONE);

        isViewInitialized = false;

        category = null;

        operand1 = null;
        operator = null;
        operand2  = null;
        resultField  = null;
        resultImage  = null;
        quesCount  = null;
        quesActivity = null;

        Random r = null;

        answer = -1;
        currQuesCount = 1;
        numRightAns = 0;

        rightAnswer = false;

        quesNAnsFrag = null;
    }
}
