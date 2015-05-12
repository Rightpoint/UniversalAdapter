package com.raizlabs.android.universaladapter.converter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.raizlabs.android.coreutils.threading.ThreadingUtils;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.ViewHolder;

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
        extends BaseAdapter implements UniversalConverter<Item, Holder> {

    private UniversalAdapter<Item, Holder> universalAdapter;

    BaseAdapterConverter(@NonNull UniversalAdapter<Item, Holder> universalAdapter,
                         AdapterView<? super BaseAdapter> adapterView) {
        setAdapter(universalAdapter);
        adapterView.setAdapter(this);
        adapterView.setOnItemClickListener(internalItemClickListener);
        adapterView.setOnItemLongClickListener(internalLongClickListener);
        notifyDataSetChanged();
    }

    // region Inherited Methods

    @Override
    public UniversalAdapter<Item, Holder> getAdapter() {
        return universalAdapter;
    }

    @Override
    public void cleanup() {
        getAdapter().getListObserver().removeListener(internalListObserverListener);
    }

    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> itemClickedListener) {
        getAdapter().setItemClickedListener(itemClickedListener);
    }

    @Override
    public void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> longClickedListener) {
        getAdapter().setItemLongClickedListener(longClickedListener);
    }

    @Override
    public void setHeaderClickedListener(HeaderClickedListener headerClickedListener) {
        getAdapter().setHeaderClickedListener(headerClickedListener);
    }

    @Override
    public void setFooterClickedListener(FooterClickedListener footerClickedListener) {
        getAdapter().setFooterClickedListener(footerClickedListener);
    }

    @Override
    public void setHeaderLongClickedListener(HeaderLongClickListener headerLongClickedListener) {
        getAdapter().setHeaderLongClickListener(headerLongClickedListener);
    }

    @Override
    public void setFooterLongClickedListener(FooterLongClickedListener footerLongClickedListener) {
        getAdapter().setFooterLongClickedListener(footerLongClickedListener);
    }

    @Override
    public void setAdapter(@NonNull UniversalAdapter<Item, Holder> universalAdapter) {
        if(getAdapter() != null) {
            getAdapter().getListObserver().removeListener(internalListObserverListener);
        }

        this.universalAdapter = universalAdapter;
        universalAdapter.getListObserver().addListener(internalListObserverListener);
    }

    @Override
    public void notifyDataSetChanged() {
        getAdapter().notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return getAdapter().getInternalItemViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return getAdapter().getInternalItemViewType(position);
    }

    @Override
    public int getCount() {
        return getAdapter().getInternalCount();
    }

    @Override
    public Item getItem(int position) {
        return getAdapter().get(position);
    }

    @Override
    public long getItemId(int position) {
        return getAdapter().getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView != null) {
            viewHolder = getViewHolder(convertView);
        }

        if (viewHolder == null) {
            int viewType = getItemViewType(position);
            viewHolder = getAdapter().createViewHolder(parent, viewType);
            setViewHolder(viewHolder.itemView, viewHolder);
        }

        getAdapter().bindViewHolder(viewHolder, position);

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
            viewHolder = getAdapter().createDropDownViewHolder(parent, viewType);
            setViewHolder(viewHolder.itemView, viewHolder);
        }

        getAdapter().bindDropDownViewHolder(viewHolder, position);

        return viewHolder.itemView;
    }

    @Override
    public boolean isEnabled(int position) {
        return getAdapter().internalIsEnabled(position);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return getAdapter().areAllItemsEnabled();
    }

    // endregion Inherited Methods

    // region Instance Methods

    protected void superNotifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    protected Holder getViewHolder(View view) {
        try {
            return UniversalAdapterUtils.getViewHolder(view);
        } catch (ClassCastException ex) {
            // Don't care. Just don't crash. We'll just ignore convertView.
        }

        return null;
    }

    /**
     * Attaches the view holder to the specified {@link View}
     *
     * @param view   The view to attach the holder to.
     * @param holder The holder to attach to the view.
     */
    protected void setViewHolder(View view, ViewHolder holder) {
        UniversalAdapterUtils.setViewHolder(view, holder);
    }

    /**
     * Calls {@link #superNotifyDataSetChanged()} on the UI thread.
     */
    protected void superNotifyDataSetChangedOnUIThread() {
        ThreadingUtils.runOnUIThread(superDataSetChangedRunnable);
    }

    // endregion Instance Methods

    // region Anonymous Classes

    private final Runnable superDataSetChangedRunnable = new Runnable() {
        @Override
        public void run() {
            superNotifyDataSetChanged();
        }
    };

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

    private final AdapterView.OnItemLongClickListener internalLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return getAdapter().onItemLongClicked(position, view);
        }
    };

    // endregion Anonymous Classes
}
