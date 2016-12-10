package edu.sjsu.cmpe277.org.mathsquizcmpe277;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by pnedunuri on 10/1/16.
 */

interface CustomDialogCallbackListener {
    public void onDialogCallback();
}

class CustomDialogFragment extends DialogFragment {
    public static final int ALERT_TYPE_ALERT = 0;
    public static final int ALERT_TYPE_CONFIRMATION = ALERT_TYPE_ALERT + 1;

    private String mTitle = null;
    private String mMsg = null;
    private int mAlertType = -1;
    private AlertDialog dialog = null;

    private CustomDialogCallbackListener mListener = null;

    public CustomDialogFragment() {

    }

    public CustomDialogFragment(CustomDialogCallbackListener listener, int alertType, String title, String msg) {
        this.mListener = listener;
        this.mAlertType = alertType;
        this.mTitle = title;
        this.mMsg = msg;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        switch (mAlertType) {
            case ALERT_TYPE_ALERT: {
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(mTitle)
                        .setMessage(mMsg)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CustomDialog.isDialogVisible = false;

                                dialog.dismiss();
                                mListener.onDialogCallback();
                            }
                        })
                        .create();
            }
            break;

            case ALERT_TYPE_CONFIRMATION: {
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(mTitle)
                        .setMessage(mMsg)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CustomDialog.isDialogVisible = false;

                                dialog.dismiss();
                                mListener.onDialogCallback();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CustomDialog.isDialogVisible = false;

                                dialog.dismiss();
                            }
                        })
                        .create();
            }
            break;
        }

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

public class CustomDialog {

    public static boolean isDialogVisible = false;
    public static int alertType = -1;

    public static void showAlert(final CustomDialogCallbackListener listener, FragmentManager manager, String title, String msg) {
        alertType = CustomDialogFragment.ALERT_TYPE_ALERT;

        CustomDialogFragment customDialogFragment = new CustomDialogFragment(listener, CustomDialogFragment.ALERT_TYPE_ALERT, title, msg);
        customDialogFragment.show(manager, "CustomDialog");

        isDialogVisible = true;
    }

    public static void showDialog(final CustomDialogCallbackListener listener, FragmentManager manager, String title, String msg) {
        alertType = CustomDialogFragment.ALERT_TYPE_CONFIRMATION;

        CustomDialogFragment customDialogFragment = new CustomDialogFragment(listener, CustomDialogFragment.ALERT_TYPE_CONFIRMATION, title, msg);
        customDialogFragment.show(manager, "CustomDialog");

        isDialogVisible = true;
    }
}