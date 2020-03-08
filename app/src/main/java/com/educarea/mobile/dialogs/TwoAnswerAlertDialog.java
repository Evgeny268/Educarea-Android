package com.educarea.mobile.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class TwoAnswerAlertDialog extends AppCompatDialogFragment {

    private TwoAnswerClickListener listener;
    private String title;
    private String message;
    private String button1Text;
    private String button2Text;

    public TwoAnswerAlertDialog(TwoAnswerClickListener listener, String title, String message, String button1Text, String button2Text) {
        this.listener = listener;
        this.title = title;
        this.message = message;
        this.button1Text = button1Text;
        this.button2Text = button2Text;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton(button1Text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onClickFirstButton();
            }
        });
        builder.setNegativeButton(button2Text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onClickSecondButton();
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }

    public interface TwoAnswerClickListener{

        void onClickFirstButton();

        void onClickSecondButton();
    }
}
