package ca.algonquin.kw2446.microdust.common;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import ca.algonquin.kw2446.microdust.R;


/**
 * Created by junsuk on 2017. 9. 7..
 */

public class AddLocationDialogFragment extends DialogFragment {

    private OnClickListener mOnClickListener;


    public interface OnClickListener {
        void onOkClicked(String city);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    private EditText mCityEditText;

    public static AddLocationDialogFragment newInstance(OnClickListener listener) {

        Bundle args = new Bundle();

        AddLocationDialogFragment fragment = new AddLocationDialogFragment();
        fragment.setOnClickListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    public AddLocationDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_location, null, false);

        mCityEditText = (EditText) view.findViewById(R.id.city_edit);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Location");
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String city = mCityEditText.getText().toString();
                mOnClickListener.onOkClicked(city);
            }
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

}
