package com.coreybutts.snore;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Home extends AppCompatActivity {
    Button buttonSleep;

    DatabaseHelper mDatabaseHelper;
    ArrayList<String> records = new ArrayList<>();

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

        ArrayList<String> records = new ArrayList<>();
        final ArrayList<String> ids = new ArrayList<>();

        Cursor data = mDatabaseHelper.getData("");
        while(data.moveToNext())
        {
            records.add(data.getString(1));
            ids.add(data.getString(0));
        }

        /*ListView listView = (ListView) findViewById(R.id.list_records);
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
        });*/

        final SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.listView);

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_home, records);
        listView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(170);

                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent resultsIntent = new Intent(Home.this, Results.class);
                resultsIntent.putExtra("ID", i);
                startActivity(resultsIntent);
                finish();
            }
        });

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        // delete
                        int new_pos = Integer.parseInt(ids.get(position));
                        mDatabaseHelper.deleteData(Integer.toString(new_pos));
                        finish();
                        startActivity(getIntent());
                        break;
                }
                // false : close the menu; true : not close the menu
                 return false;
            }
        });
    }

}
