package pt.fraunhofer.pulse.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import pt.fraunhofer.pulse.PolyGlass;
import pt.fraunhofer.pulse.R;

public class BpmDialog extends DialogFragment {

    private double bpm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.average_bpm);
        builder.setNeutralButton(android.R.string.ok, null);

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.bpm, null);
        builder.setView(dialogView);

        //getActivity().setBpm(bpm);

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        PolyGlass app = (PolyGlass)activity;
        bpm = app.getRecordedBpmAverage();
    }

}
