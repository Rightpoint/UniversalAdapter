package com.raizlabs.android.universaladapter.widget.adapters;

import android.view.ViewGroup;

import com.raizlabs.android.universaladapter.widget.adapters.converter.UniversalAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Description: Merges adapters together into one large {@link ListBasedAdapter}.
 */
public class MergedListBasedAdapter extends UniversalAdapter {

    private List<ListPiece> listPieces = new ArrayList<>();

    public void addAdapter(UniversalAdapter adapter) {
        addAdapter(listPieces.size(), adapter);
    }

    public void addAdapter(int position, UniversalAdapter adapter) {
        int count = getCount();
        ListPiece piece = new ListPiece(adapter);
        listPieces.add(position, piece);
        piece.setPositionRange(count);
        notifyDataSetChanged();
    }

    public void removeAdapter(int position) {
        listPieces.remove(position);
        // TODO: remove all positions in between
        notifyDataSetChanged();
    }

    public void removeAdapter(ListBasedAdapter adapter) {
        for (int i = 0; i < listPieces.size(); i++) {
            if (listPieces.get(i).adapter.equals(adapter)) {
                listPieces.remove(i);
                break;
            }
        }
        // TODO: remove all positions in between
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        for (ListPiece piece : listPieces) {
            piece.adapter.notifyDataSetChanged();
        }
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
            typeOffset+=piece.adapter.getItemViewTypeCount();
        }
        return viewHolder;
    }

    @Override
    public int getItemViewTypeCount() {
        int count = 0;
        for(ListPiece listPiece: listPieces) {
            count+=listPiece.adapter.getItemViewTypeCount();
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
     * Works down the position until it finds the position the adapter starts at.
     *
     * @param position The position of item in the {@link ListBasedAdapter}
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

    private static class ListPiece {

        HashSet<Integer> itemViewTypes = new HashSet<>();

        final UniversalAdapter adapter;

        int startPosition;

        int endPosition;

        ListPiece(UniversalAdapter adapter) {
            this.adapter = adapter;
        }

        void setPositionRange(int start) {
            startPosition = start;
            endPosition = startPosition + getCount();

            for (int i = 0; i < getCount(); i++) {
                itemViewTypes.add(adapter.getItemViewType(i));
            }
        }

        boolean isPositionWithinAdapter(int position) {
            return position >= startPosition && position < endPosition;
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
