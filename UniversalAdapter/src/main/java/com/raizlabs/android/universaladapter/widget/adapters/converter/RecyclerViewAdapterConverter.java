package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
import com.raizlabs.android.universaladapter.widget.adapters.RecyclerViewItemClickListener;
import com.raizlabs.android.universaladapter.widget.adapters.RecyclerViewListObserverListener;
import com.raizlabs.android.universaladapter.widget.adapters.ViewHolder;

/**
 * Class which dynamically converts a {@link ListBasedAdapter} into a
 * {@link RecyclerView.Adapter}. This keeps a binding to the
 * {@link ListBasedAdapter} so it will be notified of data changes made to the
 * outer adapter.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public class RecyclerViewAdapterConverter<Item, Holder extends ViewHolder>
        extends RecyclerView.Adapter<Holder> implements UniversalConverter<Item, Holder, RecyclerView> {

    /**
     * Provides more specific information for a click, separate from {@link ItemClickedListener}
     */
    public interface RecyclerItemClickListener<Holder extends ViewHolder> {

        /**
         * Called when an item in the {@link RecyclerView} is clicked.
         *
         * @param viewHolder The view holder of the clicked item.
         * @param parent     The recycler view which contained the clicked item.
         * @param position   The position in the adapter of the clicked item.
         */
        void onItemClick(Holder viewHolder, RecyclerView parent, int position, float x, float y);
    }

    /**
     * Helper for constructing {@link RecyclerViewAdapterConverter}s from
     * {@link ListBasedAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param listAdapter The list adapter to convert into a RecyclerView
     *                    adapter.
     * @return A RecyclerView adapter based on the given list adapter.
     */
    public static <Item, Holder extends ViewHolder>
    RecyclerViewAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> listAdapter) {
        return new RecyclerViewAdapterConverter<>(listAdapter);
    }

    /**
     * Helper for constructing {@link RecyclerViewAdapterConverter}s from
     * {@link ListBasedAdapter}s. Handles generics a little more conveniently
     * than the equivalent constructor.
     *
     * @param listAdapter  The list adapter to convert into a RecyclerView
     *                     adapter.
     * @param recyclerView The recyclerview to register.
     * @return A RecyclerView adapter based on the given list adapter.
     */
    public static <Item, Holder extends ViewHolder>
    RecyclerViewAdapterConverter<Item, Holder> from(ListBasedAdapter<Item, Holder> listAdapter, RecyclerView recyclerView) {
        return new RecyclerViewAdapterConverter<>(listAdapter, recyclerView);
    }

    private ListBasedAdapter<Item, Holder> listAdapter;

    private ItemClickedListener<Item, Holder> itemClickedListener;
    private RecyclerItemClickListener<Holder> recyclerItemClickListener;

    private RecyclerViewListObserverListener<Item> observerListener;

    public RecyclerViewAdapterConverter(ListBasedAdapter<Item, Holder> listAdapter) {
        observerListener = new RecyclerViewListObserverListener<>(this);
        setAdapter(listAdapter);
    }

    public RecyclerViewAdapterConverter(ListBasedAdapter<Item, Holder> listAdapter, RecyclerView recyclerView) {
        observerListener = new RecyclerViewListObserverListener<>(this);
        setAdapter(listAdapter);
        register(recyclerView);
    }

    /**
     * Sets the listener to be called when an item is clicked. This call back provides more
     * information about the click event of the {@link RecyclerView}
     *
     * @param recyclerItemClickListener The listener to call.
     */
    public void setRecyclerItemClickListener(RecyclerItemClickListener<Holder> recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    // region Inherited Methods

    @Override
    public ListBasedAdapter<Item, Holder> getListAdapter() {
        return listAdapter;
    }

    /**
     * Sets the listener to be called when an item is clicked.
     *
     * @param listener The listener to call.
     */
    @Override
    public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
        this.itemClickedListener = listener;
    }

    /**
     * Registers this adapter with the specified {@link RecyclerView}. It also hooks up the {@link RecyclerView.OnItemTouchListener}
     * to it.
     *
     * @param recyclerView The {@link RecyclerView} to register.
     */
    @Override
    public void register(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
        recyclerView.addOnItemTouchListener(internalOnItemTouchListener);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void cleanup() {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(observerListener);
        }
    }

    @Override
    public void setAdapter(@NonNull ListBasedAdapter<Item, Holder> listAdapter) {
        if (this.listAdapter != null) {
            this.listAdapter.getListObserver().removeListener(observerListener);
        }

        this.listAdapter = listAdapter;
        // Add a listener which will delegate list observer calls back to us
        listAdapter.getListObserver().addListener(observerListener);
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

    // endregion Inherited Methods

    private final RecyclerViewItemClickListener internalOnItemTouchListener = new RecyclerViewItemClickListener() {
        @SuppressWarnings("unchecked")
        @Override
        public void onItemClick(ViewHolder viewHolder, RecyclerView parent, int position, float x, float y) {
            if (getListAdapter().isEnabled(position)) {
                if (recyclerItemClickListener != null) {
                    recyclerItemClickListener.onItemClick((Holder) viewHolder, parent, position, x, y);
                }

                if (itemClickedListener != null) {
                    itemClickedListener.onItemClicked(getListAdapter(), getListAdapter().get(position), (Holder) viewHolder, position);
                }
            }
        }
    };

}