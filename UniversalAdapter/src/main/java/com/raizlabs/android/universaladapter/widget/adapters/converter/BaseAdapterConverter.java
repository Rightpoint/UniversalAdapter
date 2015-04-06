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

    /**
     * Helper for constructing {@link BaseAdapterConverter}s from
     * {@link UniversalAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param universalAdapter The adapter to convert into a BaseAdapter.
     * @return A BaseAdapter based on the given list adapter.
     */
    public static <Item, Holder extends ViewHolder>
    BaseAdapterConverter<Item, Holder> from(UniversalAdapter<Item, Holder> universalAdapter) {
        return new BaseAdapterConverter<>(universalAdapter);
    }

    /**
     * Helper for constructing {@link BaseAdapterConverter}s from
     * {@link UniversalAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param universalAdapter The list adapter to convert into a BaseAdapter.
     * @param adapterView      The adapter view to register.
     * @return A BaseAdapter based on the given list adapter.
     */
    public static <Item, Holder extends ViewHolder>
    BaseAdapterConverter<Item, Holder> from(UniversalAdapter<Item, Holder> universalAdapter, AdapterView<? super BaseAdapter> adapterView) {
        return new BaseAdapterConverter<>(universalAdapter, adapterView);
    }

    private UniversalAdapter<Item, Holder> universalAdapter;

    private ItemClickedListener<Item, Holder> itemClickedListener;

    private AdapterView<BaseAdapter> adapterView;

    public BaseAdapterConverter(@NonNull UniversalAdapter<Item, Holder> universalAdapter) {
        setAdapter(universalAdapter);
    }

    public BaseAdapterConverter(@NonNull UniversalAdapter<Item, Holder> universalAdapter, AdapterView<? super BaseAdapter> adapterView) {
        setAdapter(universalAdapter);
        register(adapterView);
    }

    // region Inherited Methods

    @Override
    public UniversalAdapter<Item, Holder> getAdapter() {
        return universalAdapter;
    }

    /**
     * Registers thie adapter and {@link AdapterView.OnItemClickListener} with the specified {@link AdapterView}
     *
     * @param adapterView the adapter view to register this adapter with.
     */
    @Override
    public void register(@NonNull AdapterView<? super BaseAdapter> adapterView) {
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
        if (this.universalAdapter != null) {
            this.universalAdapter.getListObserver().removeListener(internalListObserverListener);
        }
        adapterView = null;
    }

    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> itemClickedListener) {
        this.itemClickedListener = itemClickedListener;
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
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return universalAdapter.getDropDownView(position, convertView, parent);
    }

    @Override
    public int getViewTypeCount() {
        return universalAdapter.getItemViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return universalAdapter.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return universalAdapter.getCount();
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
        return universalAdapter.getView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        return universalAdapter.isEnabled(position);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return universalAdapter.areAllItemsEnabled();
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
                itemClickedListener.onItemClicked(getAdapter(), getItem(position), holder, position);
            }
        }
    };
}
