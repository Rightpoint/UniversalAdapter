package com.raizlabs.android.universaladapter.widget.adapters.viewholderstrategy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.widget.adapters.R;

/**
 * Class with some utility functions useful for {@link ViewHolderStrategy}.
 */
public class ViewHolderStrategyUtils {
    /**
     * ID of the tag in views in which the view holder should be stored.
     *
     * @see View#getTag(int, Object)
     */
    public static final int VIEWHOLDER_TAG_ID = R.id.com_raizlabs_viewholderTagID;

    /**
     * Gets and populates a view for the given item, reusing the given view if
     * it is non-null. This assumes that the convert view is a valid view for
     * this item if it is non-null. If the convert view is null, a new view
     * will be created.
     *
     * @param strategy    The strategy to use to do the population.
     * @param item        The item to populate a view for.
     * @param convertView A view which may be reused to display the item or
     *                    null to create a new one.
     * @param container   The container the view will eventually be added to,
     *                    though this implementation will not add it.
     * @return The populated view.
     */
    public static <Item, Holder> View createOrConvertView(
            ViewHolderStrategy<Item, Holder> strategy,
            Item item,
            View convertView,
            ViewGroup container) {
        if (convertView == null) {
            return createAndPopulateView(strategy, item, container);
        } else {
            populateView(strategy, convertView, item);
            return convertView;
        }
    }

    /**
     * Creates and populates a view for the given item and returns it.
     *
     * @param strategy  The strategy to use to do the creation and population.
     * @param item      The item to populate a view for.
     * @param container The container the view will eventually be added to,
     *                  though this implementation will not add it.
     * @return The populated view.
     */
    public static <Item, Holder> View createAndPopulateView(
            ViewHolderStrategy<Item, Holder> strategy,
            Item item,
            ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        return createAndPopulateView(strategy, item, inflater, container);
    }

    /**
     * Creates and populates a view for the given item and returns it.
     *
     * @param strategy  The strategy to use to do the creation and population.
     * @param item      The item to populate a view for.
     * @param inflater  An inflater to use to inflate the view.
     * @param container The container the view will eventually added to, though this
     *                  implementation will not add it.
     * @return The populated view.
     */
    public static <Item, Holder> View createAndPopulateView(
            ViewHolderStrategy<Item, Holder> strategy,
            Item item,
            LayoutInflater inflater,
            ViewGroup container) {
        View view = strategy.inflateView(item, inflater, container);
        populateView(strategy, view, item);
        return view;
    }

    /**
     * Populates the given view for the given item using the given strategy.
     *
     * @param strategy The strategy to use to populate the view.
     * @param view     The view to populate.
     * @param item     The item to populate the view with.
     */
    public static <Item, Holder> void populateView(
            ViewHolderStrategy<Item, Holder> strategy, View view, Item item) {
        if (view == null) return;

        Holder holder = getOrCreateViewHolder(strategy, view, item);

        strategy.updateView(item, holder);
    }

    /**
     * Gets the view holder associated with the given view if it is valid for
     * the given item, or creates a new one.
     *
     * @param strategy The strategy to use.
     * @param view     The view to get or create the view holder from or for.
     * @param item     The item the view represents.
     * @return The existing or created view holder.
     */
    public static <Item, Holder> Holder getOrCreateViewHolder(
            ViewHolderStrategy<Item, Holder> strategy, View view, Item item) {
        Holder holder = getViewHolderIfValid(strategy, view, item);
        if (holder == null)
            holder = createAndPopulateViewHolder(strategy, view, item);
        return holder;
    }

    /**
     * Creates a view holder for the given view representing the given item
     * and populates its pointers.
     *
     * @param strategy The strategy to use to create and populate the view
     *                 holder.
     * @param view     The view to populate the pointers from.
     * @param item     The item to represent.
     * @return The created view holder.
     */
    public static <Item, Holder> Holder createAndPopulateViewHolder(
            ViewHolderStrategy<Item, Holder> strategy, View view, Item item) {
        if (strategy == null || view == null) return null;

        Holder holder = strategy.createHolder(item);
        strategy.populateHolder(view, holder);
        setViewHolder(view, holder);

        return holder;
    }

    /**
     * Gets the view holder associated with the given view if it is valid for
     * the given item.
     *
     * @param strategy The strategy to use to check validity.
     * @param view     The view to get the view holder of.
     * @param item     The item to check the view holder against.
     * @return The associated view holder if it existed and was valid, otherwise
     * null.
     */
    @SuppressWarnings("unchecked")
    public static <Item, Holder> Holder getViewHolderIfValid(
            ViewHolderStrategy<Item, Holder> strategy, View view, Item item) {
        if (strategy == null || view == null) return null;

        try {
            // Try to cast the views view holder to the correct type
            Holder holder = (Holder) getViewHolder(view);
            // If we get here, we succeeded - ask the strategy if it's valid.
            if (strategy.isValidViewHolder(item, holder)) {
                return holder;
            }
        } catch (ClassCastException ex) {
        }

        // If we get here, it was null, couldn't be casted, or was invalid.
        return null;
    }

    /**
     * Sets the view holder associated with the given view to the given
     * view holder.
     *
     * @param view   The view to set the view holder of.
     * @param holder The view holder to associate with the given view.
     */
    public static void setViewHolder(View view, Object holder) {
        if (view != null)
            view.setTag(VIEWHOLDER_TAG_ID, holder);
    }

    /**
     * Returns the view holder associated with the given view.
     *
     * @param view The view to get the view holder of.
     * @return The associated view holder, or null if none was found.
     */
    public static Object getViewHolder(View view) {
        if (view == null) return null;
        return view.getTag(VIEWHOLDER_TAG_ID);
    }
}
