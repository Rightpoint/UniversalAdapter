package com.raizlabs.android.universaladapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.converter.MergedUniversalAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalConverterFactory;


public class MergedActivity extends ActionBarActivity {

    static final String INTENT_LAYOUT_RES = "LayoutResId";

    public static Intent getLaunchIntent(Context context, int layoutResId) {
        Intent intent = new Intent(context, MergedActivity.class);
        intent.putExtra(INTENT_LAYOUT_RES, layoutResId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getIntent().getIntExtra(INTENT_LAYOUT_RES, R.layout.activity_recyclerview));

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content);
        if (viewGroup instanceof RecyclerView) {
            ((RecyclerView) viewGroup).setLayoutManager(new LinearLayoutManager(this));
        }

        MergedUniversalAdapter adapter = new MergedUniversalAdapter();

        ListBasedAdapter<String, TextItemHolder> firstAdapter = new ListBasedAdapter<String, TextItemHolder>() {
            @Override
            protected void onBindViewHolder(TextItemHolder viewHolder, String s, int position) {
                viewHolder.Text.setText(s);
            }

            @Override
            protected TextItemHolder onCreateViewHolder(ViewGroup parent, int itemType) {
                return new TextItemHolder(inflateView(parent, R.layout.list_item_text));
            }
        };
        firstAdapter.loadItemArray("This", "Is", "A", "Test");

        adapter.addAdapter(firstAdapter);

        ListBasedAdapter<Integer, ImageItemHolder> secondAdapter = new ListBasedAdapter<Integer, ImageItemHolder>() {
            @Override
            protected void onBindViewHolder(ImageItemHolder viewHolder, Integer integer, int position) {
                viewHolder.Image.setImageResource(R.drawable.abc_btn_check_material);
            }

            @Override
            protected ImageItemHolder onCreateViewHolder(ViewGroup parent, int itemType) {
                return new ImageItemHolder(inflateView(parent, R.layout.list_item_image));
            }
        };

        secondAdapter.loadItemArray(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        adapter.addAdapter(secondAdapter);

        ListBasedAdapter<String, TextItemHolder> thirdAdapter = new ListBasedAdapter<String, TextItemHolder>() {
            @Override
            protected void onBindViewHolder(TextItemHolder viewHolder, String s, int position) {
                viewHolder.Text.setText(s);
            }

            @Override
            protected TextItemHolder onCreateViewHolder(ViewGroup parent, int itemType) {
                return new TextItemHolder(inflateView(parent, R.layout.list_item_text));
            }
        };

        thirdAdapter.loadItemArray("Footer", "Here", "This", "Is", "The", "Third", "Adapter");
        adapter.addAdapter(thirdAdapter);

        UniversalConverterFactory.createGeneric(adapter, viewGroup);
    }


    private static class TextItemHolder extends ViewHolder {

        TextView Text;

        public TextItemHolder(View itemView) {
            super(itemView);

            Text = (TextView) itemView;
        }
    }

    private static class ImageItemHolder extends ViewHolder {

        ImageView Image;

        public ImageItemHolder(View itemView) {
            super(itemView);

            Image = (ImageView) itemView;
        }
    }

}
