package com.raizlabs.widget.adapters;

import android.view.View;
import android.view.ViewGroup;

/**
 * {@link ListBasedAdapter} extension which adapts the necessary implementation
 * to be a bit more like older patterns. This can be used for compatibility for
 * older projects, but new code should use {@link ListBasedAdapter} directly.
 *
 * @param <Item> The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 * views.
 */
public abstract class ListAdapterCompat<Item, Holder> extends ListBasedAdapter<Item, ViewHolderWrapper<Holder>> {

	@Override
	public ViewHolderWrapper<Holder> onCreateViewHolder(ViewGroup parent, int itemType) {
		Holder holder = newViewHolder(itemType);
		View view = populateViewHolder(parent, holder, itemType);
		
		ViewHolderWrapper<Holder> converter = new ViewHolderWrapper<Holder>(view);
		converter.innerHolder = holder;
		
		return converter;
	}

	/**
	 * Called to construct a new empty view holder for the given item type.
	 * @param itemType The item type to create a view holder for.
	 * @return The constructed empty view holder.
	 */
	protected abstract Holder newViewHolder(int itemType);
	
	/**
	 * Called to inflate a view and populate the given view holder with the
	 * inflated views.
	 * @see #inflateView(ViewGroup, int)
	 * @param parent The parent to the view, to be used for resources.
	 * @param viewHolder The view holder to populate with the inflated views.
	 * @param itemType The view type to create a view for.
	 * @return The root view of the inflated views.
	 */
	protected abstract View populateViewHolder(ViewGroup parent, Holder viewHolder, int itemType);
	
	@Override
	public void onBindViewHolder(ViewHolderWrapper<Holder> viewHolder, Item item, int position) {
		onBindViewHolder(viewHolder.innerHolder, item, position);
	}

	/**
	 * Called to fill the views in the given view holder with the data in the
	 * given item.
	 * @param viewHolder The view holder whose views to populate.
	 * @param item The item to populate the data from.
	 * @param position The position of the item in the list.
	 */
	protected abstract void onBindViewHolder(Holder viewHolder, Item item, int position);
}
