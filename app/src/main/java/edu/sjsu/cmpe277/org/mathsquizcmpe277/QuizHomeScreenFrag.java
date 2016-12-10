package edu.sjsu.cmpe277.org.mathsquizcmpe277;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuizHomeScreenFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuizHomeScreenFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizHomeScreenFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Button addButton = null;
    private Button subButton = null;
    private Button mulButton = null;
    private Button quitButton = null;
    private static boolean isViewInitialized = false;

    MathsQuizActivity mathsQuizActivity = null;

    public QuizHomeScreenFrag() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizHomeScreenFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizHomeScreenFrag newInstance(String param1, String param2) {
        QuizHomeScreenFrag fragment = new QuizHomeScreenFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        fragment.setRetainInstance(true);
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_quiz_home_screen, container, false);

        if (isViewInitialized) {
            return v;
        }
        isViewInitialized = true;

        // Handle Buttons
        ButtonHandler handler = new ButtonHandler();

        addButton = (Button) v.findViewById(R.id.addButton);
        addButton.setOnClickListener(handler);

        subButton = (Button) v.findViewById(R.id.subButton);
        subButton.setOnClickListener(handler);

        mulButton = (Button) v.findViewById(R.id.mulButton);
        mulButton.setOnClickListener(handler);

        quitButton = (Button) v.findViewById(R.id.quitButton);
        quitButton.setOnClickListener(handler);

        mathsQuizActivity = (MathsQuizActivity) v.getContext();

        return v;
    }

    class ButtonHandler implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // Invoke new Activity
            switch (v.getId()) {
                case R.id.addButton: {
                    // Start Quiz for addition
                    mathsQuizActivity.startQuiz("ADDITION");
                }
                break;

                case R.id.subButton: {
                    // Start Quiz for subtraction
                    mathsQuizActivity.startQuiz("SUBTRACTION");
                }
                break;

                case R.id.mulButton: {
                    // Start Quiz for multiplication
                    mathsQuizActivity.startQuiz("MULTIPLICATION");
                }
                break;

                case R.id.quitButton: {
                    // quit the app
                    mathsQuizActivity.quitApp();
                }
                break;
            }
        }
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        isViewInitialized = false;

        addButton = null;
        subButton = null;
        mulButton = null;
        quitButton = null;
        mathsQuizActivity = null;
    }
}
