package com.raizlabs.android.universaladapter.converter;

import android.view.ViewGroup;

import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.android.coreutils.util.observable.lists.SimpleListObserverListener;
import com.raizlabs.android.universaladapter.ViewHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Merges adapters together into one large {@link UniversalAdapter}.
 */
public class MergedUniversalAdapter extends UniversalAdapter {

    // region Constants

    private final List<ListPiece> listPieces = new ArrayList<>();

    // endregion Constants

    // region Inherited Methods

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

        int typeOffset = 1;
        for (ListPiece piece : listPieces) {

            // offset is used to retrieve the specified item type from the inner adapter
            // since it has no knowledge of being part of this merged adapter.
            int adapterItemType = typeOffset - itemType;
            if (piece.hasViewType(adapterItemType)) {
                viewHolder = piece.adapter.createViewHolder(parent, adapterItemType);
                break;
            }

            // uses offset to calculate what the view type actually is, as offset by each adapter's viewtype amount.
            typeOffset += piece.adapter.getInternalItemViewTypeCount();
        }
        return viewHolder;
    }

    @Override
    public int getItemViewTypeCount() {
        int count = 0;
        for (ListPiece listPiece : listPieces) {
            count += listPiece.adapter.getInternalItemViewTypeCount();
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
                result = typeOffset + piece.adapter.getInternalItemViewType(position);
                break;
            }

            position -= size;
            typeOffset += piece.adapter.getInternalItemViewTypeCount();
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

    @Override
    public boolean isEnabled(int position) {
        ListPiece piece = getPieceAt(position);
        return piece.isEnabled(position);
    }

    @Override
    public long getItemId(int position) {
        ListPiece piece = getPieceAt(position);
        return piece.getItemId(position);
    }

    @Override
    public Object get(int position) {
        ListPiece piece = getPieceAt(position);
        return piece != null ? piece.getAdjustedItem(position) : null;
    }

    // endregion Inherited Methods

    // region Instance Methods

    /**
     * Adds an adapter at the end of this {@link MergedUniversalAdapter}.
     *
     * @param adapter The adapter to add to this adapter
     */
    public void addAdapter(UniversalAdapter adapter) {
        addAdapter(listPieces.size(), adapter);
        notifyDataSetChanged();
    }

    /**
     * Adds an adapter to this merged adapter based on the position of adapters.
     *
     * @param position The 0-based index position within adapters to add. If this is the 3rd adapter, the position is 2.
     * @param adapter  The adapter to add.
     */
    @SuppressWarnings("unchecked")
    void addAdapter(int position, UniversalAdapter adapter) {
        int count = getCount();

        // create reference piece
        ListPiece piece = new ListPiece(adapter);
        piece.adapter.getListObserver().addListener(cascadingListObserver);
        listPieces.add(position, piece);

        // set the starting point for it
        piece.setStartPosition(count);

        // know what kind of item types the piece contains for faster item view type.
        piece.initializeItemViewTypes();
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

    // endregion Instance Methods

    // region Anonymous Classes

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

    // endregion Anonymous Classes

    // region Inner Classes

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

        // region Instance Methods

        boolean isEnabled(int position) {
            return adapter.internalIsEnabled(getAdjustedItemPosition(position));
        }

        long getItemId(int position) {
            return adapter.getItemId(getAdjustedItemPosition(position));
        }

        public boolean hasViewType(int itemType) {
            return itemViewTypes.contains(itemType);
        }

        void setStartPosition(int position) {
            startPosition = position;
        }

        /**
         * Tracks the item view types of each adapter.
         */
        void initializeItemViewTypes() {
            for (int i = 0; i < getCount(); i++) {
                itemViewTypes.add(adapter.getInternalItemViewType(i));
            }
        }

        boolean isPositionWithinAdapter(int position) {
            return position >= startPosition && position < (startPosition + getCount());
        }

        int getCount() {
            return adapter.getInternalCount();
        }

        Object getAdjustedItem(int position) {
            return adapter.get(getAdjustedItemPosition(position));
        }

        int getAdjustedItemPosition(int position) {
            return position - startPosition;
        }

        // endregion Instance Methods

    }

    // endregion Inner Classes
}
