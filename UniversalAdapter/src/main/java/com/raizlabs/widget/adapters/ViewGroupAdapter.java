package com.raizlabs.widget.adapters;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.widget.adapters.viewholderstrategy.ViewHolderStrategyUtils;

/**
 * Class which uses a {@link ListBasedAdapter} to display a set of views in a
 * {@link ViewGroup}. This class keeps a reference to the {@link ViewGroup} and
 * lives inside the {@link ListBasedAdapter}, so you should call
 * {@link #cleanup()} when you are done with it or the {@link ViewGroup} will
 * be detached.
 *
 * @param <Item> The type of the item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 * views.
 */
public class ViewGroupAdapter<Item, Holder extends ViewHolder> {

	/**
	 * Interface for a listener which is called when a {@link ViewGroupAdapter}
	 * view is clicked.
	 */
	public interface ItemClickedListener<Item, Holder extends ViewHolder> {
		/**
		 * Called when an item in the adapter is clicked.
		 * @param adapter The adapter whose item was clicked.
		 * @param item The item which was clicked.
		 * @param holder The view holder for the clicked item.
		 * @param position The index of the clicked item in the adpater.
		 */
		public void onItemClicked(ViewGroupAdapter<Item, Holder> adapter, Item item, Holder holder, int position);
	}
	
	/**
	 * Interface for a listener which is called when a {@link ViewGroupAdapter}
	 * view is long clicked.
	 */
	public interface ItemLongClickedListener<Item, Holder extends ViewHolder> {
		/**
		 * Called when an item in the adapter is long clicked.
		 * @param adapter The adapter whose item was long clicked.
		 * @param item The item which was long clicked.
		 * @param holder The view holder for the clicked item.
		 * @param position The index of the long clicked item in the adapter.
		 * @return true if the callback consumed the long click, false otherwise.
		 */
		public boolean onItemLongClicked(ViewGroupAdapter<Item, Holder> adapter, Item item, Holder holder, int position);
	}
	
	/**
	 * Helper for constructing {@link ViewGroupAdapter}s from
	 * {@link ListBasedAdapter}s. Handles generics a little more conveniently
	 * than the equivalent constructor.
	 * @param adapter The list adapter to use to populate views.
	 * @param viewGroup The view group which will be populated with views.
	 * @return An adapter which will populate the view group via the given
	 * adapter.
	 */
	public static <Item, Holder extends ViewHolder> ViewGroupAdapter<Item, Holder> from(ListBasedAdapter<Item, Holder> adapter, ViewGroup viewGroup) {
		return new ViewGroupAdapter<Item, Holder>(viewGroup, adapter);
	}

	private ViewGroup viewGroup;
	/**
	 * @return The {@link ViewGroup} that this adapter populates.
	 */
	public ViewGroup getViewGroup() { return viewGroup; }
	
	private ListBasedAdapter<Item, Holder> listAdapter;
	/**
	 * @return The {@link ListBasedAdapter} that this adapter uses to populate
	 * the view group.
	 */
	public ListBasedAdapter<Item, Holder> getListAdapter() { return listAdapter; }

	private ItemClickedListener<Item, Holder> itemClickedListener;
	private ItemLongClickedListener<Item, Holder> itemLongClickedListener;
	
	/**
	 * Sets the listener to be called when an item is clicked.
	 * @param listener The listener to call.
	 */
	public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
		this.itemClickedListener = listener;
	}
	
	/**
	 * Sets the listener to be called when an item is long clicked.
	 * @param listener The listener to call.
	 */
	public void setItemLongClickedListener(ItemLongClickedListener<Item, Holder> listener) {
		this.itemLongClickedListener = listener;
	}
	
	/**
	 * Sets the adapter to use to populate the {@link ViewGroup} and laods
	 * the current data.
	 * @param adapter The adapter to use to populate the view group.
	 */
	public void setAdapter(ListBasedAdapter<Item, Holder> adapter) {
		if (this.listAdapter != null) {
			this.listAdapter.getListObserver().removeListener(listChangeListener);
		}
		
		this.listAdapter = adapter;
		
		if (this.listAdapter != null) {
			this.listAdapter.getListObserver().addListener(listChangeListener);
		}
		
		populateAll();
	}
	
	/**
	 * Constructs a new adapter bound to the given {@link ViewGroup}, but binds
	 * no data.
	 * @see #setAdapter(ListBasedAdapter)
	 * @param viewGroup The view group which will be populated with views.
	 */
	public ViewGroupAdapter(ViewGroup viewGroup) {
		if (viewGroup == null)
			throw new IllegalArgumentException("ViewGroup may not be null.");
		
		this.viewGroup = viewGroup;
	}
	
	/**
	 * Constructs a new adapter bound to the given {@link ViewGroup}, and binds
	 * the given adapter.
	 * @param viewGroup The view group which will be populated with views.
	 * @param adapter The list adapter to use to populate views.
	 */
	public ViewGroupAdapter(ViewGroup viewGroup, ListBasedAdapter<Item, Holder> adapter) {
		this(viewGroup);
		setAdapter(adapter);
	}
	
	/**
	 * Cleans up this adapter and disconnects it from the {@link ViewGroup} and
	 * {@link ListBasedAdapter}.
	 */
	public void cleanup() {
		setAdapter(null);
		this.viewGroup = null;
	}
	
	private void clear() {
		viewGroup.removeAllViews();
	}
	
	private void populateAll() {
		clear();
		
		if (listAdapter != null) {
			final int count = listAdapter.getCount();
			for (int i = 0; i < count; i++) {
				addItem(i);
			}
		}
	}
	
	private void addItem(int index) {
		Holder holder = listAdapter.createViewHolder(getViewGroup(), listAdapter.getItemViewType(index));
		listAdapter.bindViewHolder(holder, index);
		
		View view = holder.itemView;
		ViewHolderStrategyUtils.setViewHolder(view, holder);
		view.setOnClickListener(internalItemClickListener);
		view.setOnLongClickListener(internalItemLongClickListener);
		
		getViewGroup().addView(view, index);
	}
	
	private ListObserverListener<Item> listChangeListener = new SimpleListObserverListener<Item>() {
		@Override
		public void onGenericChange(ListObserver<Item> observer) {
			populateAll();
		}
	};
	
	private OnClickListener internalItemClickListener = new OnClickListener() {
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			if (itemClickedListener != null) {
				int index = getViewGroup().indexOfChild(v);
				Item item = listAdapter.get(index);
				Holder holder = (Holder) ViewHolderStrategyUtils.getViewHolder(v);
				itemClickedListener.onItemClicked(ViewGroupAdapter.this, item, holder, index);
			}
		}
	};
	
	private OnLongClickListener internalItemLongClickListener = new OnLongClickListener() {
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean onLongClick(View v) {
			if (itemLongClickedListener != null) {
				int index = getViewGroup().indexOfChild(v);
				Item item = listAdapter.get(index);
				Holder holder = (Holder) ViewHolderStrategyUtils.getViewHolder(v);
				return itemLongClickedListener.onItemLongClicked(ViewGroupAdapter.this, item, holder, index);
			}
			return false;
		}
	};
}
