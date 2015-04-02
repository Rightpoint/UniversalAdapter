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
import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;
import com.raizlabs.widget.adapters.R;

/**
 * Class which dynamically converts a {@link ListBasedAdapter} into a
 * {@link BaseAdapterConverter}. This keeps a binding to the
 * {@link ListBasedAdapter} so it will be notified of data changes made to the
 * outer adapter.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class BaseAdapterConverter<Item, Holder extends ViewHolder>
        extends BaseAdapter implements UniversalConverter<Item, Holder, AdapterView<BaseAdapter>> {

    /**
     * Helper for constructing {@link BaseAdapterConverter}s from
     * {@link ListBasedAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param listBasedAdapter The list adapter to convert into a BaseAdapter.
     * @return A BaseAdapter based on the given list adapter.
     */
    public static <Item, Holder extends ViewHolder>
    BaseAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> listBasedAdapter) {
        return new BaseAdapterConverter<>(listBasedAdapter);
    }

    /**
     * Helper for constructing {@link BaseAdapterConverter}s from
     * {@link ListBasedAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param listBasedAdapter The list adapter to convert into a BaseAdapter.
     * @param adapterView      The adapter view to register.
     * @return A BaseAdapter based on the given list adapter.
     */
    public static <Item, Holder extends ViewHolder>
    BaseAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> listBasedAdapter, AdapterView<BaseAdapter> adapterView) {
        return new BaseAdapterConverter<>(listBasedAdapter, adapterView);
    }

    private ListBasedAdapter<Item, Holder> listAdapter;

    private ItemClickedListener<Item, Holder> itemClickedListener;

    private AdapterView<BaseAdapter> adapterView;

    public BaseAdapterConverter(@NonNull ListBasedAdapter<Item, Holder> listAdapter) {
        setAdapter(listAdapter);
    }

    public BaseAdapterConverter(@NonNull ListBasedAdapter<Item, Holder> listAdapter, AdapterView<BaseAdapter> adapterView) {
        setAdapter(listAdapter);
        register(adapterView);
    }

    // region Inherited Methods

    @Override
    public ListBasedAdapter<Item, Holder> getListAdapter() {
        return listAdapter;
    }

    /**
     * Registers thie adapter and {@link AdapterView.OnItemClickListener} with the specified {@link AdapterView}
     *
     * @param adapterView the adapter view to register this adapter with.
     */
    @Override
    public void register(@NonNull AdapterView<BaseAdapter> adapterView) {
        adapterView.setAdapter(this);
        adapterView.setOnItemClickListener(internalItemClickListener);
        notifyDataSetChanged();
    }

    @Override
    public AdapterView<BaseAdapter> getViewGroup() {
        return adapterView;
    }

    @Override
    public void cleanup() {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(internalListObserverListener);
        }
        adapterView = null;
    }

    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
    }

    @Override
    public void setAdapter(@NonNull ListBasedAdapter<Item, Holder> listAdapter) {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(internalListObserverListener);
        }

        this.listAdapter = listAdapter;
        listAdapter.getListObserver().addListener(internalListObserverListener);
    }

    // endregion Inherited Methods

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
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return listAdapter.getDropDownView(position, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return listAdapter.getItemViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return listAdapter.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return listAdapter.getCount();
    }

    @Override
    public Item getItem(int position) {
        return listAdapter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return listAdapter.getView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        return listAdapter.isEnabled(position);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return listAdapter.areAllItemsEnabled();
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
            if (itemClickedListener != null) {
                Holder holder = (Holder) view.getTag(R.id.com_raizlabs_viewholderTagID);
                itemClickedListener.onItemClicked(getListAdapter(), getItem(position), holder, position);
            }
        }
    };
}
