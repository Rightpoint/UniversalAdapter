package com.raizlabs.universaladapter.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raizlabs.universaladapter.ListBasedAdapter;
import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.ItemClickedListener;
import com.raizlabs.universaladapter.converter.UniversalAdapter;
import com.raizlabs.universaladapter.converter.UniversalConverter;
import com.raizlabs.universaladapter.converter.UniversalConverterFactory;
import com.raizlabs.universaladapter.sample.multiple.MultipleItemTypesHFActivity;

/**
 * Description:
 */
public class MenuFragment extends Fragment {

    enum MenuConstants {

        RECYCLERVIEW {
            @Override
            public String getKeyForType() {
                return "RecyclerView";
            }

            @Override
            public int getLayoutResourceId() {
                return R.layout.activity_recyclerview;
            }
        }, LISTVIEW  {
            @Override
            public String getKeyForType() {
                return "ListView";
            }
        }, VIEWPAGER  {
            @Override
            public String getKeyForType() {
                return "ViewPager";
            }

            @Override
            public int getLayoutResourceId() {
                return R.layout.activity_viewpager;
            }
        }, VIEWGROUP {
            @Override
            public String getKeyForType() {
                return "LinearLayout ViewGroup";
            }

            @Override
            public int getLayoutResourceId() {
                return R.layout.activity_linearlayout;
            }
        };

        public abstract String getKeyForType();

        public @LayoutRes
        int getLayoutResourceId() {
            return R.layout.activity_listview;
        }

    }

    private MenuAdapter adapter;

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
        for (MenuConstants menuConstants: MenuConstants.values()) {
            for (int i = 0; i < 5; i++) {
                MenuModelHolder menuModelHolder = new MenuModelHolder();
                menuModelHolder.title = menuConstants.name();
                menuModelHolder.hasHF = (i == 1 || i == 4);
                menuModelHolder.isMerged = (i == 2);
                menuModelHolder.layoutResId = menuConstants.getLayoutResourceId();
                menuModelHolder.isMultipleItems = (i > 2);
                adapter.add(menuModelHolder);
            }
        }

        UniversalConverter converter = UniversalConverterFactory.create(adapter, recyclerView);
        converter.setItemClickedListener(new ItemClickedListener<MenuModelHolder, MenuHolder>() {
            @Override
            public void onItemClicked(UniversalAdapter<MenuModelHolder, MenuHolder> adapter, MenuModelHolder item, MenuHolder holder, int position) {
                Context context = holder.Title.getContext();
                if(item.isMultipleItems) {
                    if(item.hasHF) {
                        startActivity(MultipleItemTypesHFActivity.getHFLaunchIntent(context, item.layoutResId));
                    } else {
                        startActivity(MultipleItemTypesHFActivity.getLaunchIntent(context, item.layoutResId));
                    }
                } else if(item.isMerged) {
                    startActivity(MergedActivity.getLaunchIntent(context, item.layoutResId));
                } else if(item.hasHF) {
                    startActivity(AdapterActivity.getHFLaunchIntent(context, item.layoutResId));
                } else {
                    startActivity(AdapterActivity.getLaunchIntent(context, item.layoutResId));
                }
            }
        });
    }

    private static class MenuModelHolder {

        private String title;

        boolean isMerged;

        boolean hasHF;

        boolean isMultipleItems;

        int layoutResId;

        public String getTitle() {
            return title + (isMerged ? " - Merged" : "") + (hasHF ? " - Header + Footers" : "") + (isMultipleItems ? " - Multiple Item Types" : "");
        }
    }

    private static class MenuAdapter extends ListBasedAdapter<MenuModelHolder, MenuHolder> {

        @Override
        protected void onBindViewHolder(MenuHolder viewHolder, MenuModelHolder menuConstants, int position) {
            viewHolder.Title.setText(menuConstants.getTitle());
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
