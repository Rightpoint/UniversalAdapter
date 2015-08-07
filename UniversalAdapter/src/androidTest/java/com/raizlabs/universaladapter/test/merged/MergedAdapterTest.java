package com.raizlabs.universaladapter.test.merged;

import android.widget.LinearLayout;

import com.raizlabs.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.universaladapter.converter.MergedUniversalAdapter;
import com.raizlabs.universaladapter.converter.UniversalAdapterTestCase;
import com.raizlabs.universaladapter.test.MultipleItemTypeAdapter;

import static com.raizlabs.universaladapter.test.Constants.INDEX_CHANGED;
import static com.raizlabs.universaladapter.test.Constants.INDEX_INSERTED;
import static com.raizlabs.universaladapter.test.Constants.INDEX_REMOVED;

/**
 * Description: Tests merge adapters with all kinds of crazy adapters inside.
 */
public class MergedAdapterTest extends UniversalAdapterTestCase {

    public void testMergedAdapter() {

        LinearLayout dummyParent = new LinearLayout(getContext());

        MergedUniversalAdapter mergedUniversalAdapter = new MergedUniversalAdapter();

        MergedAdapter1 mergedAdapter1 = new MergedAdapter1();
        mergedUniversalAdapter.addAdapter(mergedAdapter1);

        mergedAdapter1.add("This");
        mergedAdapter1.add("Is");
        mergedAdapter1.add("A");
        mergedAdapter1.add("Test");

        assertEquals(4, mergedAdapter1.getCount());
        assertEquals(4, mergedUniversalAdapter.getCount());

        MultipleItemTypeAdapter multipleItemTypeAdapter = new MultipleItemTypeAdapter();
        multipleItemTypeAdapter.add("Multiple");
        multipleItemTypeAdapter.add(5);
        mergedUniversalAdapter.addAdapter(multipleItemTypeAdapter);
        assertEquals(2, multipleItemTypeAdapter.getCount());
        assertEquals(6, mergedUniversalAdapter.getCount());

        assertTrue(mergedUniversalAdapter.getAdapter(0) instanceof MergedAdapter1);
        assertTrue(mergedUniversalAdapter.getAdapter(1) instanceof MultipleItemTypeAdapter);

        for (int i = 0; i < getInternalCount(mergedUniversalAdapter); i++) {
            assertBindCorrectly(i, dummyParent, mergedUniversalAdapter);
        }

    }

    public void testMergedAdapterNotifications() {
        MergedUniversalAdapter mergedUniversalAdapter = new MergedUniversalAdapter();

        final int[] starts = new int[3];
        final int[] counts = new int[3];
        final boolean[] called = new boolean[4];
        mergedUniversalAdapter.getListObserver().addListener(new ListObserverListener() {
            @Override
            public void onItemRangeChanged(ListObserver listObserver, int start, int count) {
                starts[INDEX_CHANGED] = start;
                called[INDEX_CHANGED] = true;
                counts[INDEX_CHANGED] = count;
            }

            @Override
            public void onItemRangeInserted(ListObserver listObserver, int start, int count) {
                starts[INDEX_INSERTED] = start;
                called[INDEX_INSERTED] = true;
                counts[INDEX_INSERTED] = count;
            }

            @Override
            public void onItemRangeRemoved(ListObserver listObserver, int start, int count) {
                starts[INDEX_REMOVED] = start;
                called[INDEX_REMOVED] = true;
                counts[INDEX_REMOVED] = count;
            }

            @Override
            public void onGenericChange(ListObserver listObserver) {
                called[3] = true;
            }
        });

        // ensure adding an adapter triggers proper notification (even if empty)
        MergedAdapter1 adapter1 = new MergedAdapter1();
        mergedUniversalAdapter.addAdapter(adapter1);
        assertTrue(called[INDEX_INSERTED]);
        assertTrue(starts[INDEX_INSERTED] == 0);
        assertTrue(counts[INDEX_INSERTED] == 0);
        called[INDEX_INSERTED] = false;

        // ensure add first item works as expected
        adapter1.add("Test");
        assertTrue(called[INDEX_INSERTED]);
        assertTrue(starts[INDEX_INSERTED] == 0);
        assertTrue(counts[INDEX_INSERTED] == 1);
        called[INDEX_INSERTED] = false;

        // ensure second adapter adds where its supposed to.
        MergedAdapter1 adapter2 = new MergedAdapter1();
        mergedUniversalAdapter.addAdapter(adapter2);
        assertTrue(called[INDEX_INSERTED]);
        assertTrue(starts[INDEX_INSERTED] == 1);
        assertTrue(counts[INDEX_INSERTED] == 0);
        called[INDEX_INSERTED] = false;

        // ensure second adapter add propagates properly to merged listener
        adapter2.add("Test");
        assertTrue(called[INDEX_INSERTED]);
        assertTrue(starts[INDEX_INSERTED] == 1);
        assertTrue(counts[INDEX_INSERTED] == 1);
        called[INDEX_INSERTED] = false;

        // ensure we can "splice" an item between first and second adapter
        adapter1.add("Test");
        assertTrue(called[INDEX_INSERTED]);
        assertTrue(starts[INDEX_INSERTED] == 1);
        assertTrue(counts[INDEX_INSERTED] == 1);
        called[INDEX_INSERTED] = false;

        // ensure we can remove an item between as well
        adapter1.remove(1);
        assertTrue(called[INDEX_REMOVED]);
        assertTrue(starts[INDEX_REMOVED] == 1);
        assertTrue(counts[INDEX_REMOVED] == 1);
        called[INDEX_REMOVED] = false;

        // test prepopulated adapter inserted
        MergedAdapter1 adapter3 = new MergedAdapter1();
        adapter3.add("Test");
        adapter3.add("Another");
        mergedUniversalAdapter.addAdapter(adapter3);
        assertTrue(called[INDEX_INSERTED]);
        assertTrue(starts[INDEX_INSERTED] == 2);
        assertTrue(counts[INDEX_INSERTED] == 2);
        called[INDEX_INSERTED] = false;

        // test all 3 adapters to ensure changing data also changes correctly
        adapter1.set(0, "Test2");
        assertTrue(called[INDEX_CHANGED]);
        assertTrue(starts[INDEX_CHANGED] == 0);
        assertTrue(counts[INDEX_CHANGED] == 1);
        called[INDEX_CHANGED] = false;

        adapter2.set(0, "Test2");
        assertTrue(called[INDEX_CHANGED]);
        assertTrue(starts[INDEX_CHANGED] == 1);
        assertTrue(counts[INDEX_CHANGED] == 1);
        called[INDEX_CHANGED] = false;

        adapter3.set(0, "Test2");
        assertTrue(called[INDEX_CHANGED]);
        assertTrue(starts[INDEX_CHANGED] == 2);
        assertTrue(counts[INDEX_CHANGED] == 1);
        called[INDEX_CHANGED] = false;
    }
}
