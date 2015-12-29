package com.raizlabs.universaladapter.sample.multiple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.universaladapter.ListBasedAdapter;
import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.listeners.ItemClickedListener;
import com.raizlabs.universaladapter.converter.listeners.ItemLongClickedListener;
import com.raizlabs.universaladapter.converter.UniversalAdapter;
import com.raizlabs.universaladapter.converter.UniversalConverter;
import com.raizlabs.universaladapter.converter.UniversalConverterFactory;
import com.raizlabs.universaladapter.sample.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Description: Provides multiple kinds of adapters with headers and footers of all kinds.
 */
public class MultipleItemTypesHFActivity extends AppCompatActivity {

    static final String INTENT_LAYOUT_RESOURCE = "LayoutResource";

    static final String INTENT_USE_HF = "UseHF";

    public static Intent getLaunchIntent(Context context, int layoutResId) {
        Intent intent = new Intent(context, MultipleItemTypesHFActivity.class);
        intent.putExtra(INTENT_LAYOUT_RESOURCE, layoutResId);
        return intent;
    }

    public static Intent getHFLaunchIntent(Context context, int layoutResId) {
        Intent intent = getLaunchIntent(context, layoutResId);
        intent.putExtra(INTENT_USE_HF, true);
        return intent;
    }

    private UniversalConverter<Object, ViewHolder> converter;

    private ListBasedAdapter<Object, ? extends ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getIntent().getIntExtra(INTENT_LAYOUT_RESOURCE, R.layout.activity_viewpager);
        setContentView(layout);

        adapter = new MultipleItemTypesAdapter();

        List<Object> itemsList = new ArrayList<>();
        Random random = new Random();
        for(int i = 0; i < 50; i++) {
            int type = random.nextInt(2);
            if(type == MultipleItemTypesAdapter.VIEW_TYPE_ICON) {
                itemsList.add(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            } else {
                itemsList.add("" + i);
            }
        }

        if (getIntent().hasExtra(INTENT_USE_HF)) {
            for (int i = 0; i < 5; i++) {
                HeaderHolder headerHolder = new HeaderHolder(LayoutInflater.from(this).inflate(R.layout.list_item_image, null));
                headerHolder.Image.setImageResource(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                adapter.addHeaderHolder(headerHolder);
            }

            for (int i = 0; i < 5; i++) {
                FooterHolder footerHolder = new FooterHolder(LayoutInflater.from(this).inflate(R.layout.list_item_text, null));
                footerHolder.Dummy.setText(i + "");
                adapter.addFooterHolder(footerHolder);
            }
        }

        adapter.loadItemList(itemsList);

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content);
        if (viewGroup instanceof RecyclerView) {
            ((RecyclerView) viewGroup).setLayoutManager(new LinearLayoutManager(viewGroup.getContext()));
        }

        converter = (UniversalConverter<Object, ViewHolder>) UniversalConverterFactory.createGeneric(adapter, viewGroup);
        converter.setItemClickedListener(new ItemClickedListener<Object, ViewHolder>() {
            @Override
            public void onItemClicked(UniversalAdapter<Object, ViewHolder> adapter, Object o,
                                      ViewHolder holder, int position) {
                Toast.makeText(holder.itemView.getContext(), "Clicked on :" + position, Toast.LENGTH_SHORT).show();
            }
        });
        converter.setItemLongClickedListener(new ItemLongClickedListener<Object, ViewHolder>() {
            @Override
            public boolean onItemLongClicked(UniversalAdapter<Object, ViewHolder> adapter, Object o,
                                             ViewHolder holder, int position) {
                Toast.makeText(holder.itemView.getContext(), "Long clicked on: " + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private static class HeaderHolder extends ViewHolder {

        ImageView Image;

        public HeaderHolder(View itemView) {
            super(itemView);

            Image = (ImageView) itemView;
        }
    }

    private static class FooterHolder extends ViewHolder {

        TextView Dummy;

        public FooterHolder(View itemView) {
            super(itemView);

            Dummy = (TextView) itemView;
        }
    }
}
