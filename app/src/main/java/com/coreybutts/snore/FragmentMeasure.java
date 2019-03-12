package com.coreybutts.snore;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class FragmentMeasure extends Fragment
{
    Button buttonStart;
    TextView textSnore;
    TextView textApnea;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_measure, container, false);

        ((Sleep) getActivity()).calibrating = false;
        ((Sleep) getActivity()).calibrationStarted = false;
        ((Sleep) getActivity()).measuring = true;

        buttonStart = (Button) view.findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!((Sleep) getActivity()).measureStart)
                {
                    ((Sleep) getActivity()).serialSend("m");
                    buttonStart.setText("STOP");
                    ((Sleep) getActivity()).measureStart = true;
                }
                else
                {
                    ((Sleep) getActivity()).buttonScanOnClickProcess();
                    ((Sleep) getActivity()).openResultsActivity();
                }
            }
        });

        textSnore = (TextView) view.findViewById(R.id.text_snore);
        textApnea = (TextView) view.findViewById(R.id.text_apnea);

        return view;
    }

    public void setTextSnore(String value)
    {
        textSnore.setText(value);
    }

    public void setTextApnea(String value)
    {
        textApnea.setText(value);
    }
}

