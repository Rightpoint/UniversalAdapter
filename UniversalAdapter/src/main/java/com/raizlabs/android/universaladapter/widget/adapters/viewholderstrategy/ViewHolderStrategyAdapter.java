package com.raizlabs.android.universaladapter.widget.adapters.viewholderstrategy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.logging.Logger;
import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolderWrapper;

/**
 * Adapter class which leverages an {@link ViewHolderStrategy} to populate
 * its views. This class is provided for compatibility purposes, but is
 * non-ideal. New code should move to using {@link ListBasedAdapter} directly.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the view holder the {@link ViewHolderStrategy} uses.
 */
public class ViewHolderStrategyAdapter<Item, HolderType> extends ListBasedAdapter<Item, ViewHolderWrapper<HolderType>> {
    private ViewHolderStrategy<Item, HolderType> strategy;

    /**
     * Helper factory method for constructing {@link ViewHolderStrategyAdapter}s
     * from {@link ViewHolderStrategy}s. Handles generics a little more
     * conveniently than the equivalent constructor.
     *
     * @param strategy The view holder strategy to use to populate the adapter
     *                 views.
     * @return An adapter which produces views based on the given view holder
     * strategy.
     */
    public static <Item, HolderType> ViewHolderStrategyAdapter<Item, HolderType> from(ViewHolderStrategy<Item, HolderType> strategy) {
        return new ViewHolderStrategyAdapter<Item, HolderType>(strategy);
    }

    /**
     * Creates a {@link ViewHolderStrategyAdapter} that utilizes the given
     * {@link ViewHolderStrategy}.
     *
     * @param strategy The strategy to use to create and populate views.
     */
    public ViewHolderStrategyAdapter(ViewHolderStrategy<Item, HolderType> strategy) {
        this.strategy = strategy;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return strategy.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        Item item = get(position);
        return strategy.isEnabled(item);
    }

    @Override
    public int getItemViewType(int position) {
        Item item = get(position);
        return strategy.getItemViewType(item);
    }

    @Override
    public int getViewTypeCount() {
        return strategy.getViewTypeCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ViewHolderStrategy<Item, ?> getStrategy() {
        return this.strategy;
    }

    public void setStrategy(ViewHolderStrategy<Item, HolderType> strategy) {
        this.strategy = strategy;
        notifyDataSetChangedOnUIThread();
    }

    @Override
    public ViewHolderWrapper<HolderType> onCreateViewHolder(ViewGroup parent, int itemType) {
        Item item = null;
        // Attempt to find an item with this type.
        // This is obviously not ideal, but these paradigms don't match and this
        // is the only way we can really merge the two together.
        // This should only be called when we need to inflate views anyway, so the
        // overhead here is likely to be negligible next to view logic.
        // This class is also generally a compatibility band-aid which we should
        // moving away from using anyway.
        final int itemCount = getCount();
        for (int i = 0; i < itemCount; i++) {
            if (getItemViewType(i) == itemType) {
                item = get(i);
                break;
            }
        }

        try {
            View view = this.strategy.inflateView(item, LayoutInflater.from(parent.getContext()), parent);
            HolderType holder = ViewHolderStrategyUtils.createAndPopulateViewHolder(this.strategy, view, item);

            ViewHolderWrapper<HolderType> converter = new ViewHolderWrapper<HolderType>(view);
            converter.innerHolder = holder;

            return converter;
        } catch (NullPointerException ex) {
            // It's potentially possible we passed a null item to the strategy
            // if we couldn't find an item with the given view type
            // But this is very unlikely since the adapter shouldn't be querying
            // for types that don't exist in the list.
            // Just in case, log and return null.
            Logger.w(getClass().getSimpleName(), "Failed to create a ViewHolder of type " + itemType + ". May not have found an item of this type.", ex);
            return null;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolderWrapper<HolderType> viewHolder, Item item, int position) {
        this.strategy.updateView(item, viewHolder.innerHolder);
    }

    /**
     * Gets the {@link ViewHolderStrategy}'s view holder for the given view.
     *
     * @param view The view to get the view holder of.
     * @return The view holder associated with the view or null if none could
     * be found.
     */
    protected HolderType getStrategyViewHolder(View view) {
        ViewHolderWrapper<HolderType> holder = getViewHolder(view);
        if (holder != null) {
            return holder.innerHolder;
        }
        return null;
    }
}
