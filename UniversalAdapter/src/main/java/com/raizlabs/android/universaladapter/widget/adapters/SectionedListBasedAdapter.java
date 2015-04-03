package com.raizlabs.android.universaladapter.widget.adapters;

import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Description: An extension on {@link ListBasedAdapter} that adds section headers between
 * items in the underlying {@link ListBasedAdapter}.
 */
public abstract class SectionedListBasedAdapter<Item, Holder extends ViewHolder, SectionHolder extends ViewHolder> extends ListBasedAdapter<Item, ViewHolder> {

    private SparseArray<Integer> sectionHeaders = new SparseArray<>();

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, Item item, int position) {

    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent, int itemType) {
        if (itemType < getListItemViewTypeCount()) {
            return super.createViewHolder(parent, itemType);
        } else {
            return onCreateSectionViewHolder(parent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getItemViewTypeCount()) {
            return super.getItemViewType(position);
        } else {
            return getItemViewTypeCount();
        }
    }

    @Override
    public int getItemViewTypeCount() {
        return getListItemViewTypeCount() + 1;
    }

    private int getSectionIdForPosition(int position) {
        Integer id = sectionHeaders.get(position);
        if (id == null) {
            id = getSectionId(position, get(position));
            sectionHeaders.put(position, id);
        }
        return id;
    }

    protected int getListItemViewType(int position) {
        return 0;
    }

    protected int getListItemViewTypeCount() {
        return 1;
    }

    protected abstract SectionHolder onCreateSectionViewHolder(ViewGroup parent);

    protected abstract void onBindItemViewHolder(Holder holder, Item item, int position);

    protected abstract int getSectionId(int position, Item item);

}
