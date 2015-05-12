package com.raizlabs.universaladapter;

import android.widget.Adapter;

import com.raizlabs.android.coreutils.util.observable.lists.ObservableList;
import com.raizlabs.android.coreutils.util.observable.lists.ObservableListWrapper;
import com.raizlabs.universaladapter.converter.UniversalAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Common base class implementation of a {@link Adapter} that is backed
 * by a {@link List}. This class also implements {@link List} to support the
 * same functionality, while still functioning as an {@link UniversalAdapter}.
 *
 * @param <Item>   The type of item that views will represent.
 * @param <Holder> The type of the {@link ViewHolder} that will be used to hold
 *                 views.
 */
public abstract class ListBasedAdapter<Item, Holder extends ViewHolder> extends UniversalAdapter<Item, Holder>
        implements ObservableList<Item> {

    // region Members

    private List<Item> mList;

    // endregion Members

    // region Constructors

    /**
     * Constructs an empty {@link ListBasedAdapter}.
     */
    public ListBasedAdapter() {
        this(null);
    }

    /**
     * Constructs a {@link ListBasedAdapter} which contains the given list.
     *
     * @param list The list of items to use.
     */
    public ListBasedAdapter(List<Item> list) {
        setItemsList(list);
    }

    /**
     * Constructs a {@link ListBasedAdapter} which contains the given list. Giving this adapter's underlying data
     * a hook into notifying this class of its changes automatically.
     *
     * @param list The list of items to use.
     */
    public ListBasedAdapter(ObservableListWrapper<Item> list) {
        setItemsList(list);
    }

    // endregion Constructors

    // region Accessors

    /**
     * @return The {@link List} of items in this adapter.
     */
    protected List<Item> getItemsList() {
        return mList;
    }

    // endregion Accessors

    // region Inherited Methods

    @Override
    public void notifyDataSetChanged() {
        onGenericChange();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public void add(int location, Item object) {
        mList.add(location, object);
        onItemRangeInserted(location, 1);
    }

    @Override
    public boolean add(Item object) {
        int location = mList.size();
        final boolean result = mList.add(object);
        onItemRangeInserted(location, 1);
        return result;
    }

    @Override
    public boolean addAll(int location, Collection<? extends Item> collection) {
        if (mList.addAll(location, collection)) {
            onItemRangeInserted(location, collection.size());
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends Item> collection) {
        int location = mList.size();
        if (mList.addAll(collection)) {
            onItemRangeInserted(location, collection.size());
            return true;
        }
        return false;
    }

    @Override
    public void clear() {
        int count = size();
        mList.clear();
        onItemRangeRemoved(0, count);
    }

    @Override
    public boolean contains(Object object) {
        return mList.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return mList.containsAll(collection);
    }

    @Override
    public Item get(int location) {
        return mList.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return mList.indexOf(object);
    }

    @Override
    public Iterator<Item> iterator() {
        return mList.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return mList.lastIndexOf(object);
    }

    @Override
    public ListIterator<Item> listIterator() {
        return mList.listIterator();
    }

    @Override
    public ListIterator<Item> listIterator(int location) {
        return mList.listIterator();
    }

    @Override
    public Item remove(int location) {
        Item result = mList.remove(location);
        onItemRangeRemoved(location, 1);
        return result;
    }

    @Override
    public boolean remove(Object object) {
        int location = mList.indexOf(object);
        if (location >= 0) {
            mList.remove(location);
            onItemRangeRemoved(location, 1);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean result = mList.removeAll(collection);
        if (result) {
            onGenericChange();
        }
        return result;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean result = mList.retainAll(collection);
        if (result) {
            onGenericChange();
        }
        return result;
    }

    @Override
    public Item set(int location, Item object) {
        Item result = mList.set(location, object);
        if (!result.equals(object)) {
            onItemRangeChanged(location, 1);
        }
        return result;
    }

    @Override
    public boolean isEmpty() {
        return mList.isEmpty();
    }

    @Override
    public int size() {
        return mList.size();
    }

    @Override
    public List<Item> subList(int start, int end) {
        return mList.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return mList.toArray();
    }

    @Override
    public <S> S[] toArray(S[] array) {
        return mList.toArray(array);
    }

    //endregion Inherited Methods

    // region Instance Methods

    /**
     * Purges any resources from this adapter. Note that this may make the
     * adapter unusable.
     */
    public void cleanup() {
        unbindList();
    }

    /**
     * Loads the given varg array into a {@link List} into this adapter. See
     * {@link #notifyDataSetChangedOnUIThread()}.
     *
     * @param list The {@link List} to load.
     */
    public void loadItemArray(Item... list) {
        setItemsList(Arrays.asList(list));
    }

    /**
     * Loads the given {@link List} into this adapter. This will use the same
     * reference, so any changes to the source list will be reflected by the
     * adapter whenever the data is repopulated. See
     * {@link #notifyDataSetChangedOnUIThread()}.
     *
     * @param list The {@link List} to load.
     */
    public void loadItemList(List<Item> list) {
        setItemsList(list);
    }

    /**
     * Loads the given {@link List} into this adapter and subscribes to updates.
     * This will use the same reference, so any changes to the source list will
     * be reflected by the adapter whenever the data is repopulated. See
     * {@link #notifyDataSetChangedOnUIThread()}.
     *
     * @param list The {@link ObservableList} to load.
     */
    public void loadItemList(ObservableList<Item> list) {
        setItemsList(list);
    }

    /**
     * Loads the given items as the contents of this adapter.
     *
     * @param items The {@link Collection} of items to load.
     */
    public void loadItems(Collection<? extends Item> items) {
        List<Item> data = new ArrayList<Item>(items.size());
        for (Item item : items) {
            data.add(item);
        }
        setItemsList(data);
    }

    protected void unbindList() {
        if (mList instanceof ObservableList<?>) {
            ((ObservableList<Item>) mList).getListObserver().removeListener(observableListener);
        }
    }

    /**
     * Sets the {@link List} of items in this adapter.
     *
     * @param list The {@link List} of items to use.
     */
    protected void setItemsList(List<Item> list) {
        unbindList();
        if (list == null) {
            list = new LinkedList<Item>();
        }
        mList = list;
        notifyDataSetChangedOnUIThread();
    }

    /**
     * Sets the {@link ObservableList} of items in this adapter, and subscribes
     * to updates.
     *
     * @param list The {@link ObservableList} of items to use.
     */
    protected void setItemsList(ObservableList<Item> list) {
        if (list != null) {
            list.getListObserver().addListener(observableListener);
        }
        setItemsList((List<Item>) list);
    }

    // endregion Instance Methods

}
