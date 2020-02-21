package com.brook.wu.weatherapp.editor;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class CityNameDialog extends AlertDialog {

    public CityNameDialog(@NonNull Context context, final OnSaveCity listener) {
        super(context);
        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        setTitle("Input City Name");
        input.setLayoutParams(lp);
        setView(input);
        setButton(BUTTON_POSITIVE, "Save", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
                listener.onSave(input.getText().toString());
            }
        });
        setButton(BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
    }

   public interface OnSaveCity {
        void onSave(String cityName);
    }
}
