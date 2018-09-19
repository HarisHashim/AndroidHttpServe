package com.example.haris.httpserve;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity implements DetailFragment.OnFragmentInteractionListener, ConfigFragment.OnFragmentInteractionListener {


    private final String TAG = this.getClass().getSimpleName();

    DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        detailFragment = new DetailFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,detailFragment).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new ConfigFragment());
                fragmentTransaction.addToBackStack("");
                fragmentTransaction.commit();

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

    @Override
    public void onServerConnected(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onServerError(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onServerResponse(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onSaveConfig(boolean saved) {
        if (saved) {
            Log.d(TAG, "Config successfully changed!");
            detailFragment.reconnect();
        }else {
            Log.e(TAG, "Error while changing config!");
        }
    }
}
