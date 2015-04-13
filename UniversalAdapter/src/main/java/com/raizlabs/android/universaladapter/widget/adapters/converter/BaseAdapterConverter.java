package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.raizlabs.android.coreutils.threading.ThreadingUtils;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.widget.adapters.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.widget.adapters.R;

/**
 * Class which dynamically converts a {@link UniversalAdapter} into a
 * {@link BaseAdapterConverter}. This keeps a binding to the
 * {@link UniversalAdapter} so it will be notified of data changes made to the
 * outer adapter.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class BaseAdapterConverter<Item, Holder extends ViewHolder>
        extends BaseAdapter implements UniversalConverter<Item, Holder, AdapterView<? super BaseAdapter>> {

    private UniversalAdapter<Item, Holder> universalAdapter;


    BaseAdapterConverter(@NonNull UniversalAdapter<Item, Holder> universalAdapter, AdapterView<? super BaseAdapter> adapterView) {
        setAdapter(universalAdapter);
        adapterView.setAdapter(this);
        adapterView.setOnItemClickListener(internalItemClickListener);
        notifyDataSetChanged();
    }

    // region Inherited Methods

    @Override
    public UniversalAdapter<Item, Holder> getAdapter() {
        return universalAdapter;
    }

    @Override
    public void cleanup() {
        if (this.universalAdapter != null) {
            this.universalAdapter.getListObserver().removeListener(internalListObserverListener);
        }
    }

    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> itemClickedListener) {
        universalAdapter.setItemClickedListener(itemClickedListener);
    }

    @Override
    public void setAdapter(@NonNull UniversalAdapter<Item, Holder> listAdapter) {
        if (this.universalAdapter != null) {
            this.universalAdapter.getListObserver().removeListener(internalListObserverListener);
        }

        this.universalAdapter = listAdapter;
        listAdapter.getListObserver().addListener(internalListObserverListener);
    }

    // endregion Inherited Methods

    @Override
    public void notifyDataSetChanged() {
        universalAdapter.notifyDataSetChanged();
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
    public int getViewTypeCount() {
        return universalAdapter.getInternalItemViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return universalAdapter.getItemViewTypeInternal(position);
    }

    @Override
    public int getCount() {
        return universalAdapter.getInternalCount();
    }

    @Override
    public Item getItem(int position) {
        return universalAdapter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return universalAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = getViewHolder(convertView);
        }

        if (viewHolder == null) {
            int viewType = getItemViewType(position);
            viewHolder = universalAdapter.createViewHolder(parent, viewType);
            setViewHolder(viewHolder.itemView, viewHolder);
        }

        universalAdapter.bindViewHolder(viewHolder, position);

        return viewHolder.itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = getViewHolder(convertView);
        }

        if (viewHolder == null) {
            int viewType = getItemViewType(position);
            viewHolder = universalAdapter.createDropDownViewHolder(parent, viewType);
            setViewHolder(viewHolder.itemView, viewHolder);
        }

        universalAdapter.bindDropDownViewHolder(viewHolder, position);

        return viewHolder.itemView;
    }

    @Override
    public boolean isEnabled(int position) {
        return universalAdapter.isEnabled(position);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return universalAdapter.areAllItemsEnabled();
    }

    @SuppressWarnings("unchecked")
    protected Holder getViewHolder(View view) {
        try {
            return (Holder) UniversalAdapterUtils.getViewHolder(view);
        } catch (ClassCastException ex) {
            // Don't care. Just don't crash. We'll just ignore convertView.
        }

        return null;
    }

    protected void setViewHolder(View view, ViewHolder holder) {
        UniversalAdapterUtils.setViewHolder(view, holder);
    }

    private final ListObserverListener<Item> internalListObserverListener = new SimpleListObserverListener<Item>() {
        @Override
        public void onGenericChange(ListObserver<Item> listObserver) {
            superNotifyDataSetChangedOnUIThread();
        }
    };

    private final AdapterView.OnItemClickListener internalItemClickListener = new AdapterView.OnItemClickListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            getAdapter().onItemClicked(position, view);
        }
    };
}
