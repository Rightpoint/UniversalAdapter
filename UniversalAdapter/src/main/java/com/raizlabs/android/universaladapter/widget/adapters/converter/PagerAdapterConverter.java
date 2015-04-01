package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.threading.ThreadingUtils;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.android.universaladapter.widget.adapters.viewholderstrategy.ViewHolderStrategyUtils;

/**
 * Class which dynamically converts a {@link ListBasedAdapter} into a
 * {@link PagerAdapter}. This keeps a binding to the
 * {@link ListBasedAdapter} so it will be notified of data changes made to the
 * outer adapter.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class PagerAdapterConverter<Item, Holder extends ViewHolder> extends PagerAdapter {

    private ListBasedAdapter<Item, Holder> listAdapter;

    public ListBasedAdapter<Item, Holder> getListAdapter() {
        return listAdapter;
    }

    private ItemClickedListener<Item, Holder> itemClickedListener;
    private ItemLongClickedListener<Item, Holder> itemLongClickedListener;

    private ViewGroup viewGroup;

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public PagerAdapterConverter(ListBasedAdapter<Item, Holder> listBasedAdapter) {
        listAdapter = listBasedAdapter;
        listAdapter.getListObserver().addListener(new SimpleListObserverListener<Item>() {
            @Override
            public void onGenericChange(ListObserver<Item> listObserver) {
                superNotifyDataSetChangedOnUIThread();
            }
        });
    }

    @Override
    public void notifyDataSetChanged() {
        listAdapter.notifyDataSetChanged();
    }

    protected void superNotifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private final Runnable superDataSetChangedRunnable = new Runnable() {
        @Override
        public void run() {
            superNotifyDataSetChanged();
        }
    };

    /**
     * Calls {@link #superNotifyDataSetChanged()} on the UI thread.
     */
    protected void superNotifyDataSetChangedOnUIThread() {
        ThreadingUtils.runOnUIThread(superDataSetChangedRunnable);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (this.viewGroup == null) {
            this.viewGroup = container;
        }

        Holder holder = listAdapter.createViewHolder(container, listAdapter.getItemViewType(position));
        listAdapter.bindViewHolder(holder, position);
        View view = holder.itemView;
        ViewHolderStrategyUtils.setViewHolder(view, holder);
        view.setOnClickListener(internalItemClickListener);
        view.setOnLongClickListener(internalItemLongClickListener);
        return holder.itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return listAdapter.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private final View.OnClickListener internalItemClickListener = new View.OnClickListener() {

        @SuppressWarnings("unchecked")
        @Override
        public void onClick(View v) {
            if (itemClickedListener != null) {
                int index = getViewGroup().indexOfChild(v);
                Item item = listAdapter.get(index);
                Holder holder = (Holder) ViewHolderStrategyUtils.getViewHolder(v);
                itemClickedListener.onItemClicked(getListAdapter(), item, holder, index);
            }
        }
    };

    private View.OnLongClickListener internalItemLongClickListener = new View.OnLongClickListener() {

        @SuppressWarnings("unchecked")
        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickedListener != null) {
                int index = getViewGroup().indexOfChild(v);
                Item item = listAdapter.get(index);
                Holder holder = (Holder) ViewHolderStrategyUtils.getViewHolder(v);
                return itemLongClickedListener.onItemLongClicked(getListAdapter(), item, holder, index);
            }
            return false;
        }
    };
}
