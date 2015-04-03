package com.raizlabs.android.universaladapter;

import android.content.Context;
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
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalConverter;

/**
 * Description:
 */
public class MenuFragment extends Fragment {

    class MenuConstants {

        public static final String RECYCLERVIEW = "RecyclerView";

        public static final String RECYCLERVIEW_HOLDERS = "RecyclerView with HF Holders";

        public static final String LISTVIEW = "ListView";

        public static final String LISTVIEW_HOLDERS = "ListView with HF Holders";

        public static final String VIEWPAGER = "ViewPager";

        public static final String VIEWPAGER_HOLDERS = "ViewPager with HF Holders";

        public static final String MERGED = "Merged Recyclerview";

        public static final String MERGED_LISTVIEW = "Merged ListView";

        public static final String MERGED_VIEWPAGER = "Merged ViewPager";


    }

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
        adapter.loadItemArray(MenuConstants.RECYCLERVIEW, MenuConstants.RECYCLERVIEW_HOLDERS,
                MenuConstants.LISTVIEW, MenuConstants.LISTVIEW_HOLDERS,
                MenuConstants.VIEWPAGER, MenuConstants.VIEWPAGER_HOLDERS,
                MenuConstants.MERGED, MenuConstants.MERGED_LISTVIEW, MenuConstants.MERGED_VIEWPAGER);

        UniversalConverter converter = RecyclerViewAdapterConverter.from(adapter, recyclerView);
        converter.setItemClickedListener(new ItemClickedListener<String, MenuHolder>() {
            @Override
            public void onItemClicked(UniversalAdapter<String, MenuHolder> adapter, String s, MenuHolder holder, int position) {
                Context context = holder.Title.getContext();
                if (s.equals(MenuConstants.LISTVIEW)) {
                    startActivity(AdapterActivity.getLaunchIntent(context, R.layout.activity_listview));
                } else if (s.equals(MenuConstants.VIEWPAGER)) {
                    startActivity(AdapterActivity.getLaunchIntent(context, R.layout.activity_viewpager));
                } else if (s.equals(MenuConstants.VIEWPAGER_HOLDERS)) {
                    startActivity(AdapterActivity.getHFLaunchIntent(context, R.layout.activity_viewpager));
                } else if (s.equals(MenuConstants.LISTVIEW_HOLDERS)) {
                    startActivity(AdapterActivity.getHFLaunchIntent(context, R.layout.activity_listview));
                } else if (s.equals(MenuConstants.RECYCLERVIEW_HOLDERS)) {
                    startActivity(AdapterActivity.getHFLaunchIntent(context, R.layout.activity_recyclerview));
                } else if (s.equals(MenuConstants.MERGED)) {
                    startActivity(MergedActivity.getLaunchIntent(context, R.layout.activity_recyclerview));
                } else if (s.equals(MenuConstants.MERGED_LISTVIEW)) {
                    startActivity(MergedActivity.getLaunchIntent(context, R.layout.activity_listview));
                } else if (s.equals(MenuConstants.MERGED_VIEWPAGER)) {
                    startActivity(MergedActivity.getLaunchIntent(context, R.layout.activity_viewpager));
                } else {
                    startActivity(AdapterActivity.getLaunchIntent(context, R.layout.activity_recyclerview));
                }
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
