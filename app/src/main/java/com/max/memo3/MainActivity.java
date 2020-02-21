package com.max.memo3;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.max.memo3.TestSubject.Test0_Main;
import com.max.memo3.Util.util;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    //var
    DrawerLayout drawerLayout;

    //func
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
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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

        //action bar drawer toggle (upper left 3 line button)
//        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.app_name,R.string.app_name){
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                util.log("drawer opened");
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//                util.log("drawer closed");
//            }

//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//                util.log("drawer slide");
//            }
//        };
//        drawerToggle.setToolbarNavigationClickListener(v -> {
//            if (drawerLayout.isDrawerOpen(GravityCompat.START)){
//                drawerLayout.closeDrawer(GravityCompat.START);
//            } else {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });

        //nav drawer
        drawerLayout = findViewById(R.id.activity_main_xml);
//        drawerLayout.addDrawerListener(drawerToggle);
        NavigationView navigationView = findViewById(R.id.nav_drawer_main);
        NavController navController = Navigation.findNavController(this, R.id.activity_main_frag);
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.nav_drawer_item1:
                    navController.navigate(R.id.mainFragment_nav);
                    break;
                case R.id.nav_drawer_item2:
                    navController.navigate(R.id.currListFragment_nav);
                    break;
            }
            return true;
        });

        //set main menuItem = checked
//        navigationView.getMenu().getItem(0).setChecked(true);
    }

    //dont know why but this = press upper left menu will show nav drawer
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.activity_main_frag),drawerLayout);
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
