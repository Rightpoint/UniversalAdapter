package com.raizlabs.android.universaladapter.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.raizlabs.android.universaladapter.sample.R;


public class MainActivity extends ActionBarActivity {

    static final String TAG_MENU_FRAGMENT = "MenuFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportFragmentManager().findFragmentByTag(TAG_MENU_FRAGMENT) == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MenuFragment(), TAG_MENU_FRAGMENT)
                    .commit();
        }
    }

}
