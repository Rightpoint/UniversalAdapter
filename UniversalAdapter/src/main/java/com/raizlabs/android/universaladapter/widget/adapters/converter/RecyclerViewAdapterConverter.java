package com.raizlabs.android.universaladapter.widget.adapters.converter;

import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.universaladapter.widget.adapters.ListBasedAdapter;
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
public class RecyclerViewAdapterConverter<Item, Holder extends ViewHolder> extends RecyclerView.Adapter<Holder> {

    /**
     * Provides more specific information for a click, separate from {@link ItemClickedListener}
     */
    public interface RecyclerItemClickListener<Holder> {

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

    private ListBasedAdapter<Item, Holder> listAdapter;

    public ListBasedAdapter<Item, Holder> getListAdapter() {
        return listAdapter;
    }

    private ItemClickedListener<Item, Holder> itemClickedListener;

    private RecyclerItemClickListener<Holder> recyclerItemClickListener;

    public RecyclerViewAdapterConverter(ListBasedAdapter<Item, Holder> listAdapter) {
        this.listAdapter = listAdapter;
        // Add a listener which will delegate list observer calls back to us
        listAdapter.getListObserver().addListener(new RecyclerViewListObserverListener<Item>(this));
        setHasStableIds(listAdapter.hasStableIds());
    }

    /**
     * Sets the listener to be called when an item is clicked.
     *
     * @param listener The listener to call.
     */
    public void setItemClickedListener(ItemClickedListener<Item, Holder> listener) {
        this.itemClickedListener = listener;
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

    /**
     * Registers this adapter with the specified {@link RecyclerView}. It also hooks up the {@link RecyclerView.OnItemTouchListener}
     * to it.
     *
     * @param recyclerView The {@link RecyclerView} to register.
     */
    public void register(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
        recyclerView.addOnItemTouchListener(internalOnItemTouchListener);
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

    private final RecyclerView.OnItemTouchListener internalOnItemTouchListener = new RecyclerView.OnItemTouchListener() {

        private GestureDetector gestureDetector;

        @SuppressWarnings("unchecked")
        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            if (gestureDetector == null) {
                gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }
                });
            }

            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && gestureDetector.onTouchEvent(e)) {
                int position = view.getChildPosition(childView);
                Holder viewHolder = (Holder) view.getChildViewHolder(childView);

                if(itemClickedListener != null) {
                    itemClickedListener.onItemClicked(getListAdapter(), getListAdapter().get(position), viewHolder, position);
                }

                if(recyclerItemClickListener != null) {
                    recyclerItemClickListener.onItemClick(viewHolder, view, position, e.getX(), e.getY());
                }

            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }
    };

}