package com.coreybutts.snore;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;

import android.Manifest;
import android.widget.Toast;

public class Sleep extends BlunoLibrary {
    private Button buttonScan;
    private Button buttonSerialSend;
    private EditText serialSendText;
    private TextView serialReceivedText;

    /* MY STUFF ***********************************************************************************/
    private int ACCESS_COARSE_LOCATION_CODE = 1;


    ArrayList<XYValue> xyValueArray_s;
    ArrayList<XYValue> xyValueArray_a;
    int count;
    byte[] data;
    String a, s;
    boolean connected;
    public boolean scanning = false, calibrating = false, calibrationStarted = false, measuring = false, measureStart = false;

    Button buttonCalibrate;

    TextView snoreScore;
    TextView apneaScore;

    FragmentCalibrate fragmentCalibrate = new FragmentCalibrate();
    FragmentMeasure fragmentMeasure = new FragmentMeasure();

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        onCreateProcess();														//onCreate Process by BlunoLibrary

        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        buttonScan = (Button) findViewById(R.id.button_scan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
            }
        });

        /* MY STUFF *******************************************************************************/
        xyValueArray_s = new ArrayList<>();
        xyValueArray_a = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(Sleep.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(Sleep.this, "You have already granted this permission", Toast.LENGTH_SHORT);
        }
        else
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("ACCESS_COARSE_LOCATION needed")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Sleep.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_CODE);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_LOCATION_CODE);
            }
        }

        mDatabaseHelper = new DatabaseHelper(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ACCESS_COARSE_LOCATION_CODE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT);
            }
            else
            {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT);
            }
        }
    }

    protected void onResume(){
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();														//onResume Process by BlunoLibrary
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);					//onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();														//onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();														//onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();														//onDestroy Process by BlunoLibrary
    }

    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {											//Four connection state
            case isConnected:
                buttonScan.setText("Connected");
                connected = true;
                FragmentManager fragmentManager = getFragmentManager();
                fragmentCalibrate = new FragmentCalibrate();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(android.R.id.content, fragmentCalibrate);
                fragmentTransaction.commit();
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                buttonScan.setText("Scan");
                connected = false;
                break;
            case isScanning:
                buttonScan.setText("Scanning");
                break;
            case isDisconnecting:
                buttonScan.setText("isDisconnecting");
                break;
            default:
                break;
        }
    }

    @Override
    public void onSerialReceived(String theString)
    {
        if(calibrating)
        {
            if (calibrationStarted)
            {
                if (theString.equals("m"))
                {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentMeasure = new FragmentMeasure();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(android.R.id.content, fragmentMeasure);
                    fragmentTransaction.commit();
                }
                else
                {
                    fragmentCalibrate.setAmbientText(theString);
                }
            }

            if (theString.equals("c"))
            {
                calibrationStarted = true;
            }
        }
        else if (measuring)
        {
            s = theString.substring(theString.indexOf("s") + 1, theString.indexOf("a"));
            a = theString.substring(theString.indexOf("a") + 1);

            if (Integer.parseInt(s) > 0)
            {
                xyValueArray_s.add(new XYValue(count, (int) ((Float.parseFloat(s) / 10000f) * 100f)));
                s = Integer.toString((int) ((Float.parseFloat(s) / 10000f) * 100f));
            }
            else
            {
                xyValueArray_s.add(new XYValue(count, 0));
                s = "0";
            }
            if (Integer.parseInt(a) > 0)
            {
                xyValueArray_a.add(new XYValue(count, (int) ((Float.parseFloat(a) / 10000f) * 100f)));
                a = Integer.toString((int) ((Float.parseFloat(a) / 10000f) * 100f));
            }
            else
            {
                xyValueArray_a.add(new XYValue(count, 0));
                a = "0";
            }

            count++;

            fragmentMeasure.setTextSnore(s + "%");
            fragmentMeasure.setTextApnea(a + "%");
        }
    }

    @Override
    public void onBackPressed()
    {
        scanning = false;
        calibrating = false;
        calibrationStarted = false;
        measuring = false;
        Intent homeIntent = new Intent(Sleep.this, Home.class);
        startActivity(homeIntent);
        finish();
    }

    public void openResultsActivity()
    {
        scanning = false;
        calibrating = false;
        calibrationStarted = false;
        measuring = false;

        String date = Calendar.getInstance().getTime().toString();

        Gson gson_s = new Gson();
        String s_String = gson_s.toJson(xyValueArray_s);

        Gson gson_a = new Gson();
        String a_String = gson_a.toJson(xyValueArray_a);

        AddData(date, s_String, a_String);

        Intent resultsIntent = new Intent(Sleep.this, Results.class);
        resultsIntent.putExtra("ID", -1);
        startActivity(resultsIntent);
        finish();
    }

    public void AddData(String date_string, String s_string, String a_string)
    {
        boolean insertData = mDatabaseHelper.addData(date_string, s_string, a_string);

        if(insertData)
        {
            Toast.makeText(this, "Record Saved :D", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Something Went Wrong D:", Toast.LENGTH_SHORT).show();
        }
    }

}
