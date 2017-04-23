package com.google.sample.cloudvision;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class CompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switcheroo();
            }
        });
        setup();
    }

    public void setup() {
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));
        String month = Integer.toString(calendar.get(Calendar.MONTH));
        String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        Context context = this;
        //filename will be year month day with no spaces
        String filename = year + month + day + ".txt";
        File file = new File(context.getFilesDir(), filename);
        if (file.exists()) {
            try {
                InputStream inputStream = context.openFileInput(filename);
                TextView text;
                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    String s;
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.calories);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 2000) * 100);
                        text.append(" " + receiveString + "    " + s + "%");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.fat);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 65) * 100);
                        text.append(" " + receiveString + "g" + "    " + s + "%");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.sat_fat);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 20) * 100);
                        text.append(" " + receiveString + "g" + "    " + s + "%");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.trans_fat);
                        text.append(" " + receiveString + "g");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.cholesterol);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 300) * 100);
                        text.append(" " + receiveString + "mg" + "    " + s + "%");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.sodium);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 2400) * 100);
                        text.append(" " + receiveString + "mg" + "    " + s + "%");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.carbohydrate);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 300) * 100);
                        text.append(" " + receiveString + "g" + "    " + s + "%");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.fiber);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 25) * 100);
                        text.append(" " + receiveString + "g" + "    " + s + "%");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.sugar);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 25) * 100);
                        text.append(" " + receiveString + "g" + "    " + s + "%");
                    }
                    if ((receiveString = bufferedReader.readLine()) != null) {
                        text = (TextView) findViewById(R.id.protein);
                        s = String.format("%.1f", ((double)Integer.parseInt(receiveString) / 50) * 100);
                        text.append(" " + receiveString + "g" + "    " + s + "%");
                    }
                    inputStream.close();
                }
            }
            catch (FileNotFoundException e) {
                Log.e("login activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("login activity", "Can not read file: " + e.toString());
            }
        }
    }

    public void switcheroo(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
