package com.raizlabs.universaladapter.converter;

import android.test.AndroidTestCase;
import android.view.ViewGroup;

import com.raizlabs.universaladapter.ViewHolder;

/**
 * Description: In same package to get the benefits of internal methods for testing.
 */
public class UniversalAdapterTestCase extends AndroidTestCase {

    protected static void assertExternalItemViewType(int expected, int position, UniversalAdapter adapter) {
        assertEquals(expected, adapter.getItemViewType(position));
    }

    protected static void assertInternalItemViewType(int expected, int position, UniversalAdapter adapter) {
        assertEquals(expected, adapter.getInternalItemViewType(position));
    }

    protected static void assertInternalHolderCreatedType(int itemType, UniversalAdapter adapter, Class<?> expected,
                                                          ViewGroup parent) {
        ViewHolder viewHolder = adapter.createViewHolder(parent, itemType);
        assertTrue(viewHolder.getClass().equals(expected));
    }

    protected static void assertItemHolderCreatedType(int itemType, UniversalAdapter adapter, Class<?> expected,
                                                      ViewGroup parent) {
        ViewHolder viewHolder = adapter.onCreateViewHolder(parent, itemType);
        assertTrue(viewHolder.getClass().equals(expected));
    }

    protected static void assertTotalCount(int expected, UniversalAdapter adapter) {
        assertEquals(expected, adapter.getInternalCount());
    }

    protected static void assertBindCorrectly(int position, ViewGroup parent, UniversalAdapter adapter) {
        ViewHolder viewHolder = adapter.createViewHolder(parent, adapter.getInternalItemViewType(position));
        adapter.bindViewHolder(viewHolder, position);
    }

    protected static int getInternalCount(UniversalAdapter universalAdapter) {
        return universalAdapter.getInternalCount();
    }
}
