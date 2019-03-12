package com.coreybutts.snore;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FragmentCalibrate extends Fragment
{
    Button buttonCalibrate;
    TextView textAmbient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calibrate, container, false);

        ((Sleep) getActivity()).scanning = false;
        ((Sleep) getActivity()).calibrating = true;

        buttonCalibrate = (Button) view.findViewById(R.id.button_calibrate);
        buttonCalibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ((Sleep) getActivity()).serialSend("c");
            }
        });

        textAmbient = (TextView) view.findViewById(R.id.text_ambient);

        return view;
    }

    public void setAmbientText(String value)
    {
        textAmbient.setText(value);
    }
}
