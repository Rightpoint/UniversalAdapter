package com.raizlabs.android.universaladapter.converter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.threading.ThreadingUtils;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.UniversalAdapterUtils;
import com.raizlabs.android.universaladapter.ViewHolder;

/**
 * Class which dynamically converts a {@link UniversalAdapter} into a
 * {@link PagerAdapter}. This keeps a binding to the
 * {@link UniversalAdapter} so it will be notified of data changes made to the
 * outer adapter.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class PagerAdapterConverter<Item, Holder extends ViewHolder>
        extends PagerAdapter implements UniversalConverter<Item, Holder> {

    // region Members

    private UniversalAdapter<Item, Holder> universalAdapter;
    private ItemClickWrapper<Item, Holder> itemClickedWrapper;

    // endregion Members

    PagerAdapterConverter(@NonNull UniversalAdapter<Item, Holder> listBasedAdapter, ViewPager viewPager) {
        setAdapter(listBasedAdapter);
        viewPager.setAdapter(this);
        superNotifyDataSetChanged();
        itemClickedWrapper = new ItemClickWrapper<>(this);
    }

    // region Inherited Methods

    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
        getAdapter().setItemClickedListener(listener);
    }

    @Override
    public void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> listener) {
        getAdapter().setItemLongClickedListener(listener);
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
    public UniversalAdapter<Item, Holder> getAdapter() {
        return universalAdapter;
    }

    @Override
    public void setAdapter(@NonNull UniversalAdapter<Item, Holder> listAdapter) {
        if (getAdapter() != null) {
            getAdapter().getListObserver().removeListener(internalListObserverListener);
        }
        this.universalAdapter = listAdapter;
        listAdapter.getListObserver().addListener(internalListObserverListener);
    }

    @Override
    public void cleanup() {
        getAdapter().getListObserver().removeListener(internalListObserverListener);
    }

    @Override
    public void notifyDataSetChanged() {
        universalAdapter.notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ViewHolder holder = getAdapter().createViewHolder(container,
                                                          getAdapter().getInternalItemViewType(position));
        getAdapter().bindViewHolder(holder, position);
        View view = holder.itemView;
        UniversalAdapterUtils.setViewHolder(view, holder);
        itemClickedWrapper.register(view);
        container.addView(view);
        return holder.itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return getAdapter().getInternalCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // endregion Inherited Methods

    // region Instance Methods

    protected void superNotifyDataSetChanged() {
        super.notifyDataSetChanged();
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

    // endregion Anonymous Classes
}
