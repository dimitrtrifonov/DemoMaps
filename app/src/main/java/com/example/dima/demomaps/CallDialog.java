package com.example.dima.demomaps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Dima on 9/11/2015.
 */
public class CallDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(getArguments().get("dialog").toString())
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    boolean isClicked = false;

                    public void onClick(DialogInterface dialog, int id) {
                        //NoInternetConnectionDialog.this.isButtonClicked();
                        getActivity().finish();
                    }
            });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
