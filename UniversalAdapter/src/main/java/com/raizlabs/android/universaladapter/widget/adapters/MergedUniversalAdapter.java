package com.raizlabs.android.universaladapter.widget.adapters;

import android.view.ViewGroup;

import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: Merges adapters together into one large {@link ListBasedAdapter}.
 */
public class MergedUniversalAdapter extends UniversalAdapter {

    private List<ListPiece> listPieces = new ArrayList<>();

    public void addAdapter(UniversalAdapter adapter) {
        addAdapter(listPieces.size(), adapter);
    }

    @SuppressWarnings("unchecked")
    public void addAdapter(int position, UniversalAdapter adapter) {
        int count = getCount();

        // create reference piece
        ListPiece piece = new ListPiece(adapter);
        piece.adapter.getListObserver().addListener(cascadingListObserver);
        listPieces.add(position, piece);

        // set the starting point for it
        piece.setStartPosition(count);

        // know what kind of item types the piece contains for faster item view type.
        piece.initializeItemViewTypes();
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    public void removeAdapter(int position) {
        listPieces.remove(position)
                .adapter.getListObserver().removeListener(cascadingListObserver);
        notifyDataSetChanged();
    }

    public void removeAdapter(ListBasedAdapter adapter) {
        for (int i = 0; i < listPieces.size(); i++) {
            if (listPieces.get(i).adapter.equals(adapter)) {
                listPieces.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        onGenericChange();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, Object o, int position) {
        ListPiece piece = getPieceAt(position);
        int adjusted = piece.getAdjustedItemPosition(position);
        piece.adapter.bindViewHolder(viewHolder, adjusted);
    }

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        ViewHolder viewHolder = null;
        int typeOffset = 0;
        for (ListPiece piece : listPieces) {
            if (piece.hasViewType(typeOffset - itemType)) {
                viewHolder = piece.adapter.createViewHolder(parent, typeOffset - itemType);
                break;
            }
            typeOffset += piece.adapter.getItemViewTypeCount();
        }
        return viewHolder;
    }

    @Override
    public int getItemViewTypeCount() {
        int count = 0;
        for (ListPiece listPiece : listPieces) {
            count += listPiece.adapter.getItemViewTypeCount();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        int typeOffset = 0;
        int result = -1;

        for (ListPiece piece : listPieces) {
            int size = piece.getCount();

            if (position < size) {
                result = typeOffset + piece.adapter.getItemViewType(position);
                break;
            }

            position -= size;
            typeOffset += piece.adapter.getItemViewTypeCount();
        }

        return result;
    }

    @Override
    public int getCount() {
        int count = 0;
        for (ListPiece piece : listPieces) {
            count += piece.getCount();
        }
        return count;
    }

    /**
     * Retrieves the adapter that is for the specified position within the whole merged adapter.
     *
     * @param position The position of item in the {@link MergedUniversalAdapter}
     * @return The adapter that displays the specified position.
     */
    public ListPiece getPieceAt(int position) {
        for (ListPiece piece : listPieces) {
            if (piece.isPositionWithinAdapter(position)) {
                return piece;
            }
        }
        return null;
    }

    @Override
    public Object getItem(int position) {
        for (ListPiece piece : listPieces) {
            if (piece.isPositionWithinAdapter(position)) {
                return piece.getAdjustedItem(position);
            }
        }
        return null;
    }

    @Override
    public Object get(int location) {
        return getItem(location);
    }

    /**
     * Whenever a singular {@link ListPiece} changes, we refresh the adapter and notify content
     * changed.
     */
    private final ListObserverListener cascadingListObserver = new SimpleListObserverListener() {
        @Override
        public void onGenericChange(ListObserver listObserver) {
            notifyDataSetChanged();
        }
    };

    /**
     * Struct that keeps track of each {@link UniversalAdapter} in this merged adapter.
     */
    private static class ListPiece {

        Set<Integer> itemViewTypes = new HashSet<>();

        final UniversalAdapter adapter;

        /**
         * Position it starts at
         */
        int startPosition;

        ListPiece(UniversalAdapter adapter) {
            this.adapter = adapter;
        }

        void setStartPosition(int position) {
            startPosition = position;
        }

        /**
         * Tracks the item view types of each adapter.
         */
        void initializeItemViewTypes() {
            for (int i = 0; i < getCount(); i++) {
                itemViewTypes.add(adapter.getItemViewType(i));
            }
        }

        boolean isPositionWithinAdapter(int position) {
            return position >= startPosition && position < (startPosition + getCount());
        }

        int getCount() {
            return adapter.getCount();
        }

        public boolean hasViewType(int itemType) {
            return itemViewTypes.contains(itemType);
        }

        Object getAdjustedItem(int position) {
            return adapter.getItem(getAdjustedItemPosition(position));
        }

        int getAdjustedItemPosition(int position) {
            return position - startPosition;
        }

    }
}
