package com.raizlabs.universaladapter.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    static final String TAG_MENU_FRAGMENT = "MenuFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportFragmentManager().findFragmentByTag(TAG_MENU_FRAGMENT) == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new MenuFragment(),
                                                               TAG_MENU_FRAGMENT).commit();
        }
    }

}
