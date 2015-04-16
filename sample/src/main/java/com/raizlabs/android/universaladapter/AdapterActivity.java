package com.raizlabs.android.universaladapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.android.universaladapter.widget.adapters.converter.ItemClickedListener;
import com.raizlabs.android.universaladapter.widget.adapters.converter.ItemLongClickedListener;
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalConverter;
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalConverterFactory;

import java.util.ArrayList;
import java.util.List;


public class AdapterActivity extends FragmentActivity {

    static final String INTENT_LAYOUT_RESOURCE = "LayoutResource";

    static final String INTENT_USE_HF = "UseHF";

    public static Intent getLaunchIntent(Context context, int layoutResId) {
        Intent intent = new Intent(context, AdapterActivity.class);
        intent.putExtra(INTENT_LAYOUT_RESOURCE, layoutResId);
        return intent;
    }

    public static Intent getHFLaunchIntent(Context context, int layoutResId) {
        Intent intent = getLaunchIntent(context, layoutResId);
        intent.putExtra(INTENT_USE_HF, true);
        return intent;
    }

    private UniversalConverter<Object, UniversalHolder, ?> converter;

    private ListBasedAdapter<Object, ? extends ViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layout = getIntent().getIntExtra(INTENT_LAYOUT_RESOURCE, R.layout.activity_viewpager);
        setContentView(layout);

        adapter = new ListBasedAdapter<Object, UniversalHolder>() {
            @Override
            protected void onBindViewHolder(UniversalHolder viewHolder, Object s, int position) {
                viewHolder.Gibberish.setText(s.toString());
            }

            @Override
            protected UniversalHolder onCreateViewHolder(ViewGroup parent, int itemType) {
                return new UniversalHolder(inflateView(parent, R.layout.list_item_adapter));
            }

            @Override
            protected void onBindHeaderViewHolder(ViewHolder holder, int position) {
                HeaderHolder headerHolder = (HeaderHolder) holder;
                headerHolder.Image.setImageResource(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            }

            @Override
            protected void onBindFooterViewHolder(ViewHolder holder, int position) {
                FooterHolder footerHolder = (FooterHolder) holder;
                footerHolder.Dummy.setText("Footer: " + position);
            }
        };

        if (getIntent().hasExtra(INTENT_USE_HF)) {
            for (int i = 0; i < 5; i++) {
                HeaderHolder headerHolder = new HeaderHolder(LayoutInflater.from(this).inflate(R.layout.list_item_image, null));
                adapter.addHeaderHolder(headerHolder);
            }

            for (int i = 0; i < 5; i++) {
                FooterHolder footerHolder = new FooterHolder(LayoutInflater.from(this).inflate(R.layout.list_item_text, null));
                adapter.addFooterHolder(footerHolder);
            }
        }

        List<Object> dummyTitles = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            dummyTitles.add(String.valueOf(i));
        }

        adapter.loadItemList(dummyTitles);

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.content);
        if (viewGroup instanceof RecyclerView) {
            ((RecyclerView) viewGroup).setLayoutManager(new LinearLayoutManager(viewGroup.getContext()));
        }

        converter = (UniversalConverter<Object, UniversalHolder, ?>) UniversalConverterFactory.createGeneric(adapter, viewGroup);
        converter.setItemClickedListener(new ItemClickedListener<Object, UniversalHolder>() {
            @Override
            public void onItemClicked(UniversalAdapter<Object, UniversalHolder> adapter, Object o,
                                      UniversalHolder holder, int position) {
                Toast.makeText(holder.itemView.getContext(), "Clicked on :" + position, Toast.LENGTH_SHORT).show();
            }
        });
        converter.setItemLongClickedListener(new ItemLongClickedListener<Object, UniversalHolder>() {
            @Override
            public boolean onItemLongClicked(UniversalAdapter<Object, UniversalHolder> adapter, Object o,
                                             UniversalHolder holder, int position) {
                Toast.makeText(holder.itemView.getContext(), "Long clicked on: " + position, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private static class UniversalHolder extends ViewHolder {

        TextView Gibberish;

        public UniversalHolder(View itemView) {
            super(itemView);
            Gibberish = ((TextView) itemView);
        }
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
