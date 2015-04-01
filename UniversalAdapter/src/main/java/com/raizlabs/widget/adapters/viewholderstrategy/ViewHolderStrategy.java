package com.raizlabs.widget.adapters.viewholderstrategy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Interface which indicates that this object provides methods to create
 * and populate view holder objects for some type of item. This class is
 * often used with a List Adapter. The view holders keep references to
 * the sub views that may need to be updated to prevent looking them up
 * multiple times.
 * 
 * @see SimpleViewHolderStrategy
 *
 *@param <Item> The type of item to create views for.
 *@param <Holder> The type of the view holder this class uses.
 */
public interface ViewHolderStrategy<Item, Holder> {	
	/**
	 * @see BaseAdapter#areAllItemsEnabled()
	 * @return True if all items are enabled.
	 */
	public boolean areAllItemsEnabled();
	/**
	 * Gets whether the given item is enabled.
	 * @see BaseAdapter#isEnabled(int)
	 * @param item The item being looked up.
	 * @return True if the item is enabled.
	 */
	public boolean isEnabled(Item item);
	
	/**
	 * Gets an integer representing the view type for the given item.
	 * @see BaseAdapter#getItemViewType(int)
	 * @param item The item to get the view type for.
	 * @return An integer representing the view type.
	 */
	public int getItemViewType(Item item);
	
	/**
	 * @see BaseAdapter#getViewTypeCount()
	 * 
	 * @return The total number of view types for this strategy.
	 */
	public int getViewTypeCount();
	
	/**
	 * Inflates a View for the given item.
	 * @param item The item to inflate a View for.
	 * @param inflater The inflater to use to inflate the view.
	 * @param container The container the {@link View} will be added to. The
	 * implementation should not add the view itself.
	 * @return The inflated View.
	 */
	public View inflateView(Item item, LayoutInflater inflater, ViewGroup container);
	
	
	
	/**
	 * Creates a view holder object for the given item.
	 * @param item The item to create a view holder for.
	 * @return The created view holder.
	 */
	public Holder createHolder(Item item);
	
	/**
	 * Checks if the given view holder is a valid view holder for the given
	 * item. In simple cases, this just needs to be an instanceof check on the holder.
	 * @param item The item the view holder should be valid for.
	 * @param holder The view holder to check for validity.
	 * @return True if the view holder is valid.
	 */
	public boolean isValidViewHolder(Item item, Object holder);
	
	/**
	 * Populates the view pointers in the given view holder to point
	 * to the views in the given {@link View}. 
	 * @param view The {@link View} to populate the view holder from.
	 * @param holder The view holder to populate.
	 */
	public void populateHolder(View view, Holder holder);
	
	/**
	 * Updates the views in the given view holder to represent the given
	 * item.
	 * @param item The item the view holder should represent.
	 * @param holder The view holder to update.
	 */
	public void updateView(Item item, Holder holder);
}
