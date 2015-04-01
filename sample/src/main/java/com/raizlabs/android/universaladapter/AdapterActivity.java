package com.raizlabs.android.universaladapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.android.universaladapter.widget.adapters.converter.ConverterFactory;
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalConverter;

import java.util.ArrayList;
import java.util.List;


public class AdapterActivity extends FragmentActivity {

    static final String INTENT_LAYOUT_RESOURCE = "LayoutResource";

    public static Intent getLaunchIntent(Context context, int layoutResId) {
        Intent intent = new Intent(context, AdapterActivity.class);
        intent.putExtra(INTENT_LAYOUT_RESOURCE, layoutResId);
        return intent;
    }

    private UniversalConverter<String, UniversalHolder, ?> converter;

    private ListBasedAdapter<String, UniversalHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getIntent().getIntExtra(INTENT_LAYOUT_RESOURCE, R.layout.activity_viewpager);
        setContentView(layout);

        adapter = new ListBasedAdapter<String, UniversalHolder>() {
            @Override
            protected void onBindViewHolder(UniversalHolder viewHolder, String s, int position) {
                viewHolder.Gibberish.setText(s);
            }

            @Override
            protected UniversalHolder onCreateViewHolder(ViewGroup parent, int itemType) {
                return new UniversalHolder(new TextView(parent.getContext()));
            }
        };
        List<String> dummyTitles = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            dummyTitles.add(String.valueOf(i));
        }

        adapter.loadItemList(dummyTitles);

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content);
        if(viewGroup instanceof RecyclerView) {
            ((RecyclerView) viewGroup).setLayoutManager(new LinearLayoutManager(viewGroup.getContext()));
        }

        converter = ConverterFactory.create(adapter, viewGroup);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_adapter, menu);
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


    private static class UniversalHolder extends ViewHolder {

        TextView Gibberish;

        public UniversalHolder(View itemView) {
            super(itemView);
            Gibberish = ((TextView) itemView);
        }
    }
}
