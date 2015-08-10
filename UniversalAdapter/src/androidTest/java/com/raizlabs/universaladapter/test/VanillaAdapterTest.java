package com.raizlabs.universaladapter.test;

import android.view.View;
import android.widget.LinearLayout;

import com.raizlabs.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.UniversalAdapterTestCase;

import static com.raizlabs.universaladapter.test.Constants.INDEX_CHANGED;
import static com.raizlabs.universaladapter.test.Constants.INDEX_GENERIC;
import static com.raizlabs.universaladapter.test.Constants.INDEX_INSERTED;
import static com.raizlabs.universaladapter.test.Constants.INDEX_REMOVED;

/**
 * Description: Tests to ensure vanilla adapters (not merged) handle item types and other data appropriately.
 */
public class VanillaAdapterTest extends UniversalAdapterTestCase {

    public void testVanillaAdapter() {

        VanillaAdapter adapter = new VanillaAdapter();

        LinearLayout viewGroup = new LinearLayout(getContext());

        String name = "test";
        adapter.add(name);

        assertTrue(adapter.size() == 1);

        int number = 6;
        adapter.add(number);

        assertTrue(adapter.size() == 2);

        View header = new View(getContext());
        adapter.addHeaderView(header);
        assertTrue(adapter.getHeadersCount() == 1);

        View footer = new View(getContext());
        adapter.addFooterView(footer);
        assertTrue(adapter.getFootersCount() == 1);

        // still 2
        assertEquals(adapter.size(), 2);

        /// header view type is correct
        assertInternalItemViewType(0, 0, adapter);
        assertInternalHolderCreatedType(0, adapter, ViewHolder.class, viewGroup);

        // footer is expected
        assertInternalItemViewType(2, 3, adapter);
        assertInternalHolderCreatedType(2, adapter, ViewHolder.class, viewGroup);

        assertInternalItemViewType(1, 1, adapter);
        assertInternalItemViewType(1, 2, adapter);
        assertInternalHolderCreatedType(1, adapter, VanillaAdapter.VanillaHolder.class, viewGroup);

    }


    public void testVanillaListeners() {
        // keep track of changes
        final boolean[] changes = new boolean[4];
        VanillaAdapter adapter = new VanillaAdapter();
        adapter.getListObserver().addListener(new ListObserverListener<Object>() {
            @Override
            public void onItemRangeChanged(ListObserver<Object> listObserver, int i, int i1) {
                changes[INDEX_CHANGED] = true;
            }

            @Override
            public void onItemRangeInserted(ListObserver<Object> listObserver, int i, int i1) {
                changes[INDEX_INSERTED] = true;
            }

            @Override
            public void onItemRangeRemoved(ListObserver<Object> listObserver, int i, int i1) {
                changes[INDEX_REMOVED] = true;
            }

            @Override
            public void onGenericChange(ListObserver<Object> listObserver) {
                changes[INDEX_GENERIC] = true;
            }
        });

        adapter.add("Test");
        assertTrue(changes[INDEX_INSERTED]);

        adapter.set(0, "Test2");
        assertTrue(changes[INDEX_CHANGED]);

        adapter.remove("Test2");
        assertTrue(changes[INDEX_REMOVED]);

        adapter.notifyDataSetChanged();
        assertTrue(changes[INDEX_GENERIC]);
    }
}
