package com.juliusmh.remotegeofix;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private TextView tv_log;

    MockLocationHttpdListener httpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // UI
        tv_log = (TextView) findViewById(R.id.tv_log);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                httpd = new MockLocationHttpdListener(4444, getApplicationContext(), new MockLocationHttpdListener.HttpdListener(){

                    @Override
                    public void onExit() {

                        AsyncTask<String, Void, String> asyncExit = new AsyncTask<String, Void, String>(){

                            @Override
                            protected String doInBackground(String... params) {
                                try {Thread.sleep(1000);} catch (InterruptedException e) {}

                                httpd.closeAllConnections();
                                httpd.stop();

                                log("Server stopped");
                                return null;
                            }
                        };

                        asyncExit.execute();

                    }

                });

                try {
                    httpd.start();
                    log("Server started successfully");
                } catch (IOException e) {
                    log("Server could not be started");
                    e.printStackTrace();
                }
            }
        });
    }

    public void log(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_log.append("\n"+ txt);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
