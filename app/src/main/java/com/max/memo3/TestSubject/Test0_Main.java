package com.max.memo3.TestSubject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.max.memo3.R;
import com.max.memo3.Util.util;

public class Test0_Main extends AppCompatActivity {
    //var
    private Test_ViewModel viewModel;

    //func
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test0_main);
//        viewModel = ViewModelProviders.of(this).get(Test_ViewModel.class);
        viewModel = new ViewModelProvider(this).get(Test_ViewModel.class);

        //util init
        util.setCurrActivity(this);

        //test5 + set top textview
        Observer<String> topTextObserver = s -> ((TextView)findViewById(R.id.Test0_main_fragment_text)).setText(s);
        viewModel.getMsgToShow().observe(this,topTextObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_test0,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_test_button1:
                util.log("menu_test_button1 pressed");
                break;
            case R.id.menu_test_button2:
                util.log("menu_test_button2 pressed");
                break;
            case R.id.menu_test_item1:
                util.log("menu_test_item1 pressed");
                break;
            case R.id.menu_test_item2:
                util.log("menu_test_item2 pressed");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        if (Navigation.findNavController(this,R.id.Test0_main_fragment_frag).getCurrentDestination().getLabel().toString().equalsIgnoreCase("Test0_frag")) {
//        if (Navigation.findNavController(this,R.id.Test0_main_fragment_frag).getCurrentDestination().getId()==R.id.test0_frag) {
        util.log(viewModel.getName().getValue());
        if (viewModel.getName().getValue().compareTo("Test0")==0) {
            super.onBackPressed();
        } else {
            Navigation.findNavController(this,R.id.Test0_main_fragment_frag).navigate(R.id.test0_Main_nav);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        util.log("qwer");
    }
}
