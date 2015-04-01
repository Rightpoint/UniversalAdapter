package com.raizlabs.widget.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Class which dynamically converts a {@link ListBasedAdapter} into a
 * {@link RecyclerView.Adapter}. This keeps a binding to the
 * {@link ListBasedAdapter} so it will be notified of data changes made to the
 * outer adapter.
 *
 * @param <Item> The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 * views.
 */
public class RecyclerViewAdapterConverter<Item, Holder extends ViewHolder> extends RecyclerView.Adapter<Holder> {

	/**
	 * Helper for constructing {@link RecyclerViewAdapterConverter}s from
	 * {@link ListBasedAdapter}s. Handles generics a little more conveniently
	 * than the equivalent constructor.
	 * @param listAdapter The list adapter to convert into a RecyclerView
	 * adapter.
	 * @return A RecyclerView adapter based on the given list adapter.
	 */
	public static <Item, Holder extends ViewHolder> RecyclerViewAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> listAdapter) {
		return new RecyclerViewAdapterConverter<Item, Holder>(listAdapter);
	}
	
	private ListBasedAdapter<Item, Holder> listAdapter;
	public ListBasedAdapter<Item, Holder> getListAdapter() { return listAdapter; }
	
	public RecyclerViewAdapterConverter(ListBasedAdapter<Item, Holder> listAdapter) {
		this.listAdapter = listAdapter;
		// Add a listener which will delegate list observer calls back to us
		listAdapter.getListObserver().addListener(new RecyclerViewListObserverListener<Item>(this));
		setHasStableIds(listAdapter.hasStableIds());
	}
	
	@Override
	public long getItemId(int position) {
		return listAdapter.getItemId(position);
	}
	
	@Override
	public int getItemViewType(int position) {
		return listAdapter.getItemViewType(position);
	}
	
	@Override
	public int getItemCount() {
		return listAdapter.getCount();
	}

	@Override
	public void onBindViewHolder(Holder viewHolder, int itemType) {
		listAdapter.bindViewHolder(viewHolder, itemType);
	}

	@Override
	public Holder onCreateViewHolder(ViewGroup parent, int position) {
		return listAdapter.createViewHolder(parent, position);
	}
	
}