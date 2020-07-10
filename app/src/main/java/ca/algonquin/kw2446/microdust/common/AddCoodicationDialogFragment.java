package ca.algonquin.kw2446.microdust.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import ca.algonquin.kw2446.microdust.R;



public class AddCoodicationDialogFragment extends DialogFragment {

    private OnClickListener mOnClickListener;


    public interface OnClickListener {
        void onOkClicked(double lat, double lng);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    private EditText etLat, etLng;

    public static AddCoodicationDialogFragment newInstance(OnClickListener listener) {

        Bundle args = new Bundle();

        AddCoodicationDialogFragment fragment = new AddCoodicationDialogFragment();
        fragment.setOnClickListener(listener);
        fragment.setArguments(args);
        return fragment;
    }

    public AddCoodicationDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_coodinates, null, false);

        etLat = (EditText) view.findViewById(R.id.etLatitude);
        etLng= (EditText) view.findViewById(R.id.etLongtitude);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Location by Coodinate");
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mOnClickListener.onOkClicked(Double.parseDouble(etLat.getText().toString().trim())
                        ,Double.parseDouble(etLng.getText().toString().trim()));
            }
        });
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

}
