package com.coreybutts.snore;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class Home extends AppCompatActivity {
    Button buttonSleep;

    DatabaseHelper mDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonSleep = (Button) findViewById(R.id.button_sleep);

        buttonSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sleepIntent = new Intent(Home.this, Sleep.class);
                startActivity(sleepIntent);
                finish();
            }
        });

        mDatabaseHelper = new DatabaseHelper(this);

        /*SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        db.delete("sleep_records", null, null);*/

        ArrayList<String> records = new ArrayList<>();

        Cursor data = mDatabaseHelper.getData("");
        while(data.moveToNext())
        {
            records.add(data.getString(1));
        }

        ListView listView = (ListView) findViewById(R.id.list_records);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_home, records);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent resultsIntent = new Intent(Home.this, Results.class);
                resultsIntent.putExtra("ID", i);
                startActivity(resultsIntent);
                finish();
            }
        });

    }

}
