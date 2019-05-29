package com.coreybutts.snore;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Results extends AppCompatActivity
{
    ArrayList<XYValue> xyValueArray_s;
    ArrayList<XYValue> xyValueArray_a;
    ArrayList<String> timestamps_arraylist;
    PointsGraphSeries<DataPoint> xySeries_s;
    PointsGraphSeries<DataPoint> xySeries_a;
    GraphView mLinePlot;

    DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        mDatabaseHelper = new DatabaseHelper(this);

        int ID = getIntent().getIntExtra("ID", -1);

        Cursor data = mDatabaseHelper.getData("");
        data.moveToFirst();

        if(ID > -1)
        {
          while(data != null)
          {
              if(Integer.parseInt(data.getString(0)) == ID)
              {
                  break;
              }
              if(!data.moveToNext())
              {
                  data.moveToLast();
                  break;
              }
          }
        }
        else
        {
            data.moveToLast();
        }

        Type listType = new TypeToken<ArrayList<XYValue>>(){}.getType();
        xyValueArray_s = new Gson().fromJson(data.getString(2), listType);
        xyValueArray_a = new Gson().fromJson(data.getString(3), listType);
        timestamps_arraylist = new Gson().fromJson(data.getString(4), new TypeToken<ArrayList<String>>(){}.getType());

        mLinePlot =(GraphView) findViewById(R.id.line_plot);
        /*xyValueArray_s = (ArrayList<XYValue>) getIntent().getSerializableExtra("xyValueArray_s");
        xyValueArray_a = (ArrayList<XYValue>) getIntent().getSerializableExtra("xyValueArray_a");*/
        xySeries_s = new PointsGraphSeries<>();
        xySeries_a = new PointsGraphSeries<>();

        mLinePlot.getViewport().setMinY(0);
        mLinePlot.getViewport().setMaxY(100);
        mLinePlot.getViewport().setYAxisBoundsManual(true);
        mLinePlot.setBackgroundColor(Color.rgb(55, 71, 79));
        mLinePlot.getGridLabelRenderer().setGridColor(Color.rgb(255,255,255));
        mLinePlot.getGridLabelRenderer().setVerticalLabelsColor(Color.rgb(255,255,255));
        mLinePlot.getGridLabelRenderer().setHorizontalLabelsColor(Color.rgb(255,255,255));
        mLinePlot.getGridLabelRenderer().setHorizontalAxisTitle("Time");
        mLinePlot.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.rgb(255,255,255));
        mLinePlot.getGridLabelRenderer().setVerticalAxisTitle("Score (%)");
        mLinePlot.getGridLabelRenderer().setVerticalAxisTitleColor(Color.rgb(255,255,255));
        mLinePlot.setTitle("Your Sleep History");
        mLinePlot.setTitleColor(Color.rgb(255,255,255));

        if (xyValueArray_s.size() > 100)
        {
            ArrayList<XYValue> xyValueArray_s_new = new ArrayList<>();
            ArrayList<XYValue> xyValueArray_a_new = new ArrayList<>();
            ArrayList<String> timestamps_new = new ArrayList<>();
            int count = 0;
            for(int i = 0; i < xyValueArray_a.size(); i += xyValueArray_a.size()/100)
            {
                if(i < xyValueArray_a.size())
                {
                    count++;
                    xyValueArray_a_new.add(xyValueArray_a.get(i));
                    xyValueArray_s_new.add(xyValueArray_s.get(i));
                    if(count >= 20)
                    {
                        count = 0;
                        timestamps_new.add(timestamps_arraylist.get(i));
                    }
                }
            }

            xyValueArray_a = xyValueArray_a_new;
            xyValueArray_s = xyValueArray_s_new;
            //timestamps_arraylist = timestamps_new;
        }

        createPlot();
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
        mLinePlot.getViewport().setMaxX(xyValueArray_s.get(xyValueArray_s.size()-1).getX());
        mLinePlot.getViewport().setXAxisBoundsManual(true);

        mLinePlot.addSeries(xySeries_s);
        mLinePlot.addSeries(xySeries_a);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(mLinePlot);
        final String[] timestamps = timestamps_arraylist.toArray(new String[0]);
        mLinePlot.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return timestamps[(int) value];
                }
                else
                {
                    return String.format("%d", (int) value);
                }
            }

            @Override
            public void setViewport(Viewport viewport) {

            }
        });
        /*staticLabelsFormatter.setHorizontalLabels(timestamps);
        mLinePlot.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);*/
        GridLabelRenderer renderer = mLinePlot.getGridLabelRenderer();
        renderer.setHorizontalLabelsAngle(45);
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
        Intent homeIntent = new Intent(Results.this, Home.class);
        startActivity(homeIntent);
        finish();
    }
}
