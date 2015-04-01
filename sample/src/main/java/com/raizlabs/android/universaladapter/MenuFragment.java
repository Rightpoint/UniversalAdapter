package com.raizlabs.android.universaladapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.android.universaladapter.widget.adapters.converter.ItemClickedListener;
import com.raizlabs.android.universaladapter.widget.adapters.converter.RecyclerViewAdapterConverter;
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalConverter;

/**
 * Description:
 */
public class MenuFragment extends Fragment {

    private ListBasedAdapter<String, MenuHolder> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        adapter = new MenuAdapter();
        adapter.loadItemArray("RecyclerView", "ListView", "ViewPager");

        UniversalConverter converter = RecyclerViewAdapterConverter.from(adapter, recyclerView);
        converter.setItemClickedListener(new ItemClickedListener<String, MenuHolder>() {
            @Override
            public void onItemClicked(ListBasedAdapter<String, MenuHolder> adapter, String s, MenuHolder holder, int position) {
                int layoutResId = R.layout.activity_recyclerview;
                if(s.equals("ListView")) {
                    layoutResId = R.layout.activity_listview;
                } else if(s.equals("ViewPager")) {
                    layoutResId = R.layout.activity_viewpager;
                }

                startActivity(AdapterActivity.getLaunchIntent(holder.Title.getContext(), layoutResId));

            }
        });
    }

    private static class MenuAdapter extends ListBasedAdapter<String, MenuHolder> {

        @Override
        protected void onBindViewHolder(MenuHolder viewHolder, String s, int position) {
            viewHolder.Title.setText(s);
        }

        @Override
        protected MenuHolder onCreateViewHolder(ViewGroup parent, int itemType) {
            return new MenuHolder(inflateView(parent, R.layout.list_item_menu));
        }
    }

    private static class MenuHolder extends ViewHolder {

        TextView Title;

        public MenuHolder(View itemView) {
            super(itemView);

            Title = ((TextView) itemView);
        }
    }
}
