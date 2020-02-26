package com.max.memo3;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.max.memo3.TestSubject.Test0_Main;
import com.max.memo3.Util.util;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    //var
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        navigationView = findViewById(R.id.nav_drawer_main);
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

        //nav drawer header set name + set onClick login
        if (util.isGoogleSignIned()){
            ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_icon);
            Glide.with(this).load(util.getGoogleAccount().getPhotoUrl()).circleCrop().into(imageView);
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_name)).setText(util.getGoogleAccount().getDisplayName());
            ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_email)).setText(util.getGoogleAccount().getEmail());
        }
        ((LinearLayout)navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id)).setOnClickListener(v -> {
            if (util.isGoogleSignIned()){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You sure you want to logout?");
                builder.setPositiveButton("Yes",(dialog, which) -> {
                    dialog.dismiss();
                    util.makeGoogleSignIn_signOut();
                    ((ImageView)navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_icon)).setImageResource(R.mipmap.ic_launcher_round);
                    ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_name)).setText("Not signed in");
                    ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_email)).setText("Not signed in");
                });
                builder.setNegativeButton("No",(dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();
            } else {
                util.makeGoogleSignIn_signIn();
            }
        });

    }

    //dont know why but this = press upper left menu will show nav drawer
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.activity_main_frag),drawerLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case util.googleSignInActivityRequestCode:
                util.makeGoogleSignIn_handleSignIn(data);
                util.log(util.getGoogleAccount().getPhotoUrl().toString());
                ImageView imageView = navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_icon);
                Glide.with(this).load(util.getGoogleAccount().getPhotoUrl()).circleCrop().into(imageView);
                ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_name)).setText(util.getGoogleAccount().getDisplayName());
                ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_drawer_header_id).findViewById(R.id.nav_drawer_header_email)).setText(util.getGoogleAccount().getEmail());
                break;
        }
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
