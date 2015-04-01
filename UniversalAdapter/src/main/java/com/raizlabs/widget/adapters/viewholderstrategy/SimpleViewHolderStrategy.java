package com.raizlabs.widget.adapters.viewholderstrategy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Class which provides a base for implementing a {@link ViewHolderStrategy}
 * with one view type. Automatically does some of the "boiler plate" code
 * that is consistent in many implementations and allows quick implementation
 * via a few abstract functions.
 * 
 * @param <Item> The type of item to create views for.
 * @param <Holder> The type of the view holder this class uses.
 */
public abstract class SimpleViewHolderStrategy<Item, Holder> 
implements ViewHolderStrategy<Item, Holder> {
	/**
	 * Gets the resource ID of the layout which should be inflated for the given item.
	 * @param item The item to get a layout resource ID for.
	 * @return The resource ID of the layout to be inflated for the given item.
	 */
	protected abstract int getLayoutResID(Item item);
	
	public View inflateView(Item item, LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(getLayoutResID(item), container, false);
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(Item item) {
		return true;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public boolean isValidViewHolder(Item item, Object holder) {
		try {
			Holder typed = (Holder) holder;
			return true;
		} catch (ClassCastException ex) { }
		return false;
	}
	
	@Override
	public int getItemViewType(Item item) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}
}
