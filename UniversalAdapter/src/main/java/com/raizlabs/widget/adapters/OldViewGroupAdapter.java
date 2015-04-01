package com.raizlabs.widget.adapters;

import java.util.LinkedList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;


/**
 * Class which binds a set of data to {@link View}s in a given {@link ViewGroup}.
 *
 * @param <T> The type of the data to bind.
 * @deprecated Use {@link ViewGroupAdapter} instead.
 */
public abstract class OldViewGroupAdapter<T> {
	ViewGroup viewGroup;
	/**
	 * Gets the {@link ViewGroup} this adapter is bound to.
	 * @return The {@link ViewGroup} this adapter is bound to.
	 */
	public ViewGroup getViewGroup() { return viewGroup; }
	
	private List<T> items;
	/**
	 * Gets the list of items currently loaded into this adapter.
	 * @return The list of items.
	 */
	public List<T> getCurrentItems() { return items; }
	
	private boolean createClickListeners = true;
	public void setCreateClickListeners(boolean createClickListeners) { this.createClickListeners = createClickListeners; }
	public boolean createsClickListeners() { return createClickListeners; }
	
	public interface ItemClickedListener<T> {
		public void onItemClicked(OldViewGroupAdapter<T> adapter, T item, int index);
	}
	private ItemClickedListener<T> itemClickedListener;
	/**
	 * Sets the listener to be called when an item is clicked.
	 * @param listener The listener to call.
	 */
	public void setItemClickedListener(ItemClickedListener<T> listener) {
		this.itemClickedListener = listener;
	}
	
	public interface ItemLongClickedListener<T> {
		/**
		 * Called when an item in the adapter is long clicked.
		 * @param adapter The adapter whose item was long clicked.
		 * @param item The item which was long clicked.
		 * @param index The index of the long clicked item
		 * @return true if the callback consumed the long click, false otherwise.
		 */
		public boolean onItemLongClicked(OldViewGroupAdapter<T> adapter, T item, int index);
	}
	private ItemLongClickedListener<T> itemLongClickedListener;
	/**
	 * Sets the listener to be called when an item is long clicked.
	 * @param listener The listener to call.
	 */
	public void setItemLongClickedListener(ItemLongClickedListener<T> listener) {
		this.itemLongClickedListener = listener;
	}
	
	private LayoutInflater getLayoutInflater() {
		return LayoutInflater.from(viewGroup.getContext());
	}

	/**
	 * Creates a {@link OldViewGroupAdapter} bound to the given {@link ViewGroup}.
	 * @param viewGroup The {@link ViewGroup} to bind to.
	 */
	public OldViewGroupAdapter(ViewGroup viewGroup) {
		if (viewGroup == null)
			throw new IllegalArgumentException("ViewGroup may not be null.");
		
		this.viewGroup = viewGroup;
		items = new LinkedList<T>();
	}
	
	/**
	 * Loads the given items as the data set of this adapter. This replaces all
	 * existing items.
	 * @param items The items to set as the data.
	 */
	public void load(Iterable<T> items) {
		viewGroup.removeAllViews();
		this.items.clear();
		add(items);
	}
	
	/**
	 * Adds the given item to the end of the data set of this adapter.
	 * @param item The item to add.
	 */
	public void add(T item) {
		addItem(item, getLayoutInflater());
	}
	
	/**
	 * Adds the given items to the end of the data set of this adapter.
	 * @param items The items to add.
	 */
	public void add(Iterable<T> items) {
		if (items == null) return;
		final LayoutInflater inflater = getLayoutInflater();
		for (T item : items) {
			addItem(item, inflater);
		}
	}
	
	
	/**
	 * Removes all items from the data set of this adapter.
	 */
	public void clear() {
		items.clear();
		viewGroup.removeAllViews();
	}
	
	/**
	 * Returns the index of the first occurrence of the given item.
	 * @param item The item to retrieve the index of.
	 * @return The first index of the given item or -1 if it was not found.
	 */
	public int indexOf(T item) {
		return items.indexOf(item);
	}
	
	/**
	 * Removes the first occurrence of the given item from the data set of this
	 * adapter.
	 * @param item The item to remove.
	 * @return True if the item was removed, false if it was not.
	 */
	public boolean remove(T item) {
		if (item == null) return false;
		final int index = items.indexOf(item);
		return item.equals(removeAt(index));
	}
	
	/**
	 * Removes the item at the given position in this adapter.
	 * @param position The index to remove from.
	 * @return The item that was removed.
	 */
	public T removeAt(int position) {
		T item = items.remove(position);
		View view = getViewForIndex(position);
		viewGroup.removeView(view);
		return item;
	}
	
	/**
	 * Finds the view in the {@link ViewGroup} for the given position.
	 * Default implementation passes this call through to {@link ViewGroup#getChildAt(int)}.
	 * Override this method if your child implementation is customizing the
	 * arrangement or positions of child views in this adapter.
	 * @param position The position of the view you would like to retrieve
	 * @return The {@link View} at the given position in the {@link ViewGroup}.
	 */
	public View getViewForIndex(int position) {
		return viewGroup.getChildAt(position);
	}
	
	protected void addItem(final T item, LayoutInflater inflater) {
		items.add(item);
		View view = createView(item, inflater, viewGroup);
		viewGroup.addView(view);
		
		if (createClickListeners) {
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (itemClickedListener != null) {
						final int index = viewGroup.indexOfChild(v);
						itemClickedListener.onItemClicked(OldViewGroupAdapter.this, item, index);
					}
				}
			});
			view.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if (itemLongClickedListener != null) {
						final int index = viewGroup.indexOfChild(v);
						return itemLongClickedListener.onItemLongClicked(OldViewGroupAdapter.this, item, index);
					}
					return false;
				}
			});
		}
	}
	
	/**
	 * Called to get the {@link View} to be displayed for the given item.
	 * @param item The item to get the view for.
	 * @param inflater A {@link LayoutInflater} to use to create the view.
	 * @param root The container the {@link View} will be added to. The
	 * implementation should not add the view itself.
	 * @return The {@link View} to display for the given item.
	 */
	protected abstract View createView(T item, LayoutInflater inflater, ViewGroup root);
}
