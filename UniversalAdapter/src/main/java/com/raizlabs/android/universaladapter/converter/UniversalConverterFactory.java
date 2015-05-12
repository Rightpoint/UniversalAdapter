package com.raizlabs.android.universaladapter.converter;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.raizlabs.android.universaladapter.ViewHolder;

/**
 * Class which contains methods for creating various {@link UniversalConverter}s.
 */
public class UniversalConverterFactory {

    /**
     * Takes an {@link UniversalAdapter} and a {@link ViewGroup} and creates an
     * {@link UniversalConverter} that is most appropriate for the {@link ViewGroup}.
     * If no better binding is known, this will fall back to creating a
     * {@link ViewGroupAdapterConverter}.
     * @param adapter   The adapter to create the converter for.
     * @param viewGroup The view group to create the converter for.
     * @param <Item>    The type of data being bound to the views.
     * @param <Holder>  The type of view holder being used for views.
     * @return An {@link UniversalConverter} created to bind the given adapter to
     * the given view group.
     */
    @SuppressWarnings("unchecked")
    public static <Item, Holder extends ViewHolder>
    UniversalConverter<Item, Holder> createGeneric(UniversalAdapter<Item, Holder> adapter, ViewGroup viewGroup) {
        if (viewGroup instanceof RecyclerView) {
            return create(adapter, (RecyclerView) viewGroup);
        } else if (viewGroup instanceof AdapterView) {
            try {
                return create(adapter, (AdapterView<? super BaseAdapter>) viewGroup);
            } catch (ClassCastException e) {
                return create(adapter, viewGroup);
            }
        } else if (viewGroup instanceof ViewPager) {
            return create(adapter, (ViewPager) viewGroup);
        } else {
            return create(adapter, viewGroup);
        }
    }

    /**
     * Creates a {@link ViewGroupAdapterConverter} that can populate the given {@link ViewGroup}.
     * @param adapter   The adapter to create the converter for.
     * @param viewGroup The view group to create the converter for.
     * @param <Item>    The type of data being bound to the views.
     * @param <Holder>  The type of view holder being used for views.
     * @return A {@link ViewGroupAdapterConverter} created to bind the given adapter to
     * the given view group.
     */
    public static <Item, Holder extends ViewHolder>
    ViewGroupAdapterConverter<Item, Holder> create(UniversalAdapter<Item, Holder> adapter, ViewGroup viewGroup) {
        return new ViewGroupAdapterConverter<>(adapter, viewGroup);
    }

    /**
     * Creates a {@link RecyclerViewAdapterConverter} that can populate the given {@link RecyclerView}.
     * @param adapter   The adapter to create the converter for.
     * @param recyclerView  The recycler view to create the converter for.
     * @param <Item>    The type of data being bound to the views.
     * @param <Holder>  The type of view holder being used for the views.
     * @return A {@link RecyclerViewAdapterConverter} created to bind the given
     * adapter to the given recycler view.
     */
    public static <Item, Holder extends ViewHolder>
    RecyclerViewAdapterConverter<Item, Holder> create(UniversalAdapter<Item, Holder> adapter, RecyclerView recyclerView) {
        return new RecyclerViewAdapterConverter<>(adapter, recyclerView);
    }

    /**
     * Creates a {@link PagerAdapterConverter} that can populate the given {@link ViewPager}.
     * @param adapter   The adapter to create the converter for.
     * @param viewPager The view pager to create the converter for.
     * @param <Item>    The type of data being bound to the views.
     * @param <Holder>  The type of view holder being used for the views.
     * @return A {@link PagerAdapterConverter} created to bind the given
     * adapter to the given view pager.
     */
    public static <Item, Holder extends ViewHolder>
    PagerAdapterConverter<Item, Holder> create(UniversalAdapter<Item, Holder> adapter, ViewPager viewPager) {
        return new PagerAdapterConverter<>(adapter, viewPager);
    }

    /**
     * Creates a {@link BaseAdapterConverter} that can populate the given {@link AdapterView}.
     * @param adapter   The adapter to create the converter for.
     * @param adapterView   The adapter view to create the converter for.
     * @param <Item>    The type of data being bound to the views.
     * @param <Holder>  The type of view holder being used for the views.
     * @return A {@link BaseAdapterConverter} created to bind the given adapter
     * to the given adapter view.
     */
    public static <Item, Holder extends ViewHolder>
    BaseAdapterConverter<Item, Holder> create(UniversalAdapter<Item, Holder> adapter, AdapterView<? super BaseAdapter> adapterView) {
        return new BaseAdapterConverter<>(adapter, adapterView);
    }
}
