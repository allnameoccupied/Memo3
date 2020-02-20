package com.max.memo3;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.max.memo3.TestSubject.Test0_Main;
import com.max.memo3.Util.util;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //util init msg
        util.log("STARTSTART GOGOGO");
        util.setAppContext(getApplicationContext());
        util.setCurrActivity(this);
        util.INIT();

        //toolbar 1
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //toolbar 2
//        NavController navController = Navigation.findNavController(this,)

        //floating button
//        FloatingActionButton fab = findViewById(R.id.main_fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());

        //button
//        Button button = findViewById(R.id.main_button);
//        button.setOnClickListener(v -> {
//            util.log("pressed main button");
//            startActivity(new Intent(getApplicationContext(), Test0_Main.class));
//        });

        //nav drawer

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
        if (id == R.id.menu_main_settings) {
//            util.makeToast("menu_main_settings pressed");
            util.log("menu_main_settings pressed");
            return true;
        }
        if (id == R.id.menu_main_test) {
//            util.makeToast("menu_main_test pressed");
            util.log("menu_main_test pressed");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
