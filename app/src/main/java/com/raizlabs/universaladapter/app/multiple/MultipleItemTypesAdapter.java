package com.raizlabs.universaladapter.app.multiple;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.raizlabs.universaladapter.ListBasedAdapter;
import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.app.R;

/**
 * Description: Tests multiple different item types.
 */
public class MultipleItemTypesAdapter extends ListBasedAdapter<Object, ViewHolder> {

    static final int VIEW_TYPE_ICON = 0;
    static final int VIEW_TYPE_TEXT = 1;

    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int itemType) {
        switch (itemType) {
            case VIEW_TYPE_ICON:
                return new ImageItemHolder(inflateView(parent, R.layout.list_item_image));
            case VIEW_TYPE_TEXT:
                return new TextItemHolder(inflateView(parent, R.layout.list_item_text));
        }
        throw new IllegalArgumentException("Invalid itemType found of: " + itemType);
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, Object o, int position) {
        if (getItemViewType(position) == VIEW_TYPE_TEXT) {
            ((TextItemHolder) viewHolder).text.setText(String.valueOf(o));
        } else if (getItemViewType(position) == VIEW_TYPE_ICON) {
            ((ImageItemHolder) viewHolder).image.setImageResource((Integer) get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object item = get(position);
        if(item instanceof Integer) {
            return VIEW_TYPE_ICON;
        } else if(item instanceof String) {
            return VIEW_TYPE_TEXT;
        }
        throw new IllegalArgumentException("Invalid item found");
    }

    @Override
    public int getItemViewTypeCount() {
        return 2;
    }

    private static class TextItemHolder extends ViewHolder {

        TextView text;

        public TextItemHolder(View itemView) {
            super(itemView);

            text = (TextView) itemView;
        }
    }

    private static class ImageItemHolder extends ViewHolder {

        ImageView image;

        public ImageItemHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView;
        }
    }
}
