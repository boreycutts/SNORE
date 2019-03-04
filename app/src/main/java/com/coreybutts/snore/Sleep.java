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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import android.Manifest;
import android.widget.Toast;

public class Sleep extends BlunoLibrary {
    private Button buttonScan;
    private Button buttonSerialSend;
    private EditText serialSendText;
    private TextView serialReceivedText;

    /* MY STUFF ***********************************************************************************/
    private int ACCESS_COARSE_LOCATION_CODE = 1;

    PointsGraphSeries<DataPoint> xySeries_s;
    PointsGraphSeries<DataPoint> xySeries_a;
    GraphView mLinePlot;
    ArrayList<XYValue> xyValueArray_s;
    ArrayList<XYValue> xyValueArray_a;
    int count;
    byte[] data;
    String a, s;
    boolean connected;

    Button buttonCalibrate;

    TextView snoreScore;
    TextView apneaScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        onCreateProcess();														//onCreate Process by BlunoLibrary


        serialBegin(115200);													//set the Uart Baudrate on BLE chip to 115200

        //serialReceivedText=(TextView) findViewById(R.id.serialReveicedText);	//initial the EditText of the received data
        //serialSendText=(EditText) findViewById(R.id.serialSendText);			//initial the EditText of the sending data

        buttonScan = (Button) findViewById(R.id.buttonScan);					//initial the button for scanning the BLE device
        buttonScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                buttonScanOnClickProcess();										//Alert Dialog for selecting the BLE device
            }
        });

        /* MY STUFF *******************************************************************************/



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
                FragmentCalibrate fragmentCalibrate = new FragmentCalibrate();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(android.R.id.content, fragmentCalibrate);
                fragmentTransaction.commit();
                break;
            case isConnecting:
                buttonScan.setText("Connecting");
                break;
            case isToScan:
                if(connected)
                {
                    //createPlot();
                }
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
        try
        {
            s = theString.substring(theString.indexOf("s") + 1, theString.indexOf("a"));
            a = theString.substring(theString.indexOf("a") + 1);

            if (Integer.parseInt(s) > 0)
            {
                xyValueArray_s.add(new XYValue(count, (int) ((Float.parseFloat(s) / 10000f) * 100f)));
                s = Integer.toString((int) ((Float.parseFloat(s) / 10000f) * 100f));
            }
            if (Integer.parseInt(a) > 0)
            {
                xyValueArray_a.add(new XYValue(count, (int) ((Float.parseFloat(a) / 10000f) * 100f)));
                a = Integer.toString((int) ((Float.parseFloat(a) / 10000f) * 100f));
            }

            count++;

            snoreScore.setText(s + "%");
            apneaScore.setText(a + "%");
        }
        catch(Exception e)
        {

        }
    }

    private void createPlot()
    {
        mLinePlot.removeAllSeries();
        xyValueArray_s = sortArray(xyValueArray_s);
        xyValueArray_a = sortArray(xyValueArray_a);

        for(int i = 0; i < xyValueArray_s.size(); i++)
        {
            double x = xyValueArray_s.get(i).getX();
            double y = xyValueArray_s.get(i).getY();
            xySeries_s.appendData(new DataPoint(x, y), true, 1000);
        }
        for(int i = 0; i < xyValueArray_a.size(); i++)
        {
            double x = xyValueArray_a.get(i).getX();
            double y = xyValueArray_a.get(i).getY();
            xySeries_a.appendData(new DataPoint(x, y), true, 1000);
        }

        xySeries_s.setShape(PointsGraphSeries.Shape.POINT);
        xySeries_s.setColor(Color.rgb(112, 112, 244));
        xySeries_s.setSize(5f);

        xySeries_a.setShape(PointsGraphSeries.Shape.POINT);
        xySeries_a.setColor(Color.rgb(244, 112, 112));
        xySeries_a.setSize(5f);

        //set Scrollable and Scaleable
        mLinePlot.getViewport().setScalable(true);
        mLinePlot.getViewport().setScalableY(false);
        mLinePlot.getViewport().setScrollable(true);
        mLinePlot.getViewport().setScrollableY(false);

        mLinePlot.getViewport().setMinY(0);
        mLinePlot.getViewport().setMaxY(100);
        mLinePlot.getViewport().setYAxisBoundsManual(true);

        mLinePlot.getViewport().setMinX(0);
        mLinePlot.getViewport().setMaxX(count);
        mLinePlot.getViewport().setXAxisBoundsManual(true);

        mLinePlot.addSeries(xySeries_s);
        mLinePlot.addSeries(xySeries_a);
    }

    private ArrayList<XYValue> sortArray(ArrayList<XYValue> array){
        /*
        //Sorts the xyValues in Ascending order to prepare them for the PointsGraphSeries<DataSet>
         */
        int factor = Integer.parseInt(String.valueOf(Math.round(Math.pow(array.size(),2))));
        int m = array.size() - 1;
        int count = 0;


        while (true) {
            m--;
            if (m <= 0) {
                m = array.size() - 1;
            }
            try {
                //print out the y entrys so we know what the order looks like
                //Log.d(TAG, "sortArray: Order:");
                //for(int n = 0;n < array.size();n++){
                //Log.d(TAG, "sortArray: " + array.get(n).getY());
                //}
                double tempY = array.get(m - 1).getY();
                double tempX = array.get(m - 1).getX();
                if (tempX > array.get(m).getX()) {
                    array.get(m - 1).setY(array.get(m).getY());
                    array.get(m).setY(tempY);
                    array.get(m - 1).setX(array.get(m).getX());
                    array.get(m).setX(tempX);
                } else if (tempX == array.get(m).getX()) {
                    count++;
                } else if (array.get(m).getX() > array.get(m - 1).getX()) {
                    count++;
                }
                //break when factorial is done
                if (count == factor) {
                    break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.getMessage();
                break;
            }
        }
        return array;
    }

    @Override
    public void onBackPressed()
    {
        Intent homeIntent = new Intent(Sleep.this, Home.class);
        startActivity(homeIntent);
        finish();
    }

}
