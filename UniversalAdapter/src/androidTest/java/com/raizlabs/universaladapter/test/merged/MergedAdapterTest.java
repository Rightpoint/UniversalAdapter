package com.raizlabs.universaladapter.test.merged;

import android.widget.LinearLayout;

import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.universaladapter.converter.MergedUniversalAdapter;
import com.raizlabs.universaladapter.converter.UniversalAdapterTestCase;
import com.raizlabs.universaladapter.test.MultipleItemTypeAdapter;

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

        for(int i = 0; i < getInternalCount(mergedUniversalAdapter); i++) {
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
                starts[0] = start;
                called[0] = true;
                counts[0] = count;
            }

            @Override
            public void onItemRangeInserted(ListObserver listObserver, int start, int count) {
                starts[1] = start;
                called[1] = true;
                counts[1] = count;
            }

            @Override
            public void onItemRangeRemoved(ListObserver listObserver, int start, int count) {
                starts[2] = start;
                called[2] = true;
                counts[2] = count;
            }

            @Override
            public void onGenericChange(ListObserver listObserver) {
                called[3] = true;
            }
        });

        // ensure adding an adapter triggers proper notification (even if empty)
        MergedAdapter1 adapter1 = new MergedAdapter1();
        mergedUniversalAdapter.addAdapter(adapter1);
        assertTrue(called[1]);
        assertTrue(starts[1] == 0);
        assertTrue(counts[1] == 0);
        called[1] = false;

        // ensure add first item works as expected
        adapter1.add("Test");
        assertTrue(called[1]);
        assertTrue(starts[1] == 0);
        assertTrue(counts[1] == 1);
        called[1] = false;

        // ensure second adapter adds where its supposed to.
        MergedAdapter1 adapter2 = new MergedAdapter1();
        mergedUniversalAdapter.addAdapter(adapter2);
        assertTrue(called[1]);
        assertTrue(starts[1] == 1);
        assertTrue(counts[1] == 0);
        called[1] = false;

        // ensure second adapter add propagates properly to merged listener
        adapter2.add("Test");
        assertTrue(called[1]);
        assertTrue(starts[1] == 1);
        assertTrue(counts[1] == 1);
        called[1] = false;

        // ensure we can "splice" an item between first and second adapter
        adapter1.add("Test");
        assertTrue(called[1]);
        assertTrue(starts[1] == 1);
        assertTrue(counts[1] == 1);
        called[1] = false;

        // ensure we can remove an item between as well
        adapter1.remove(1);
        assertTrue(called[2]);
        assertTrue(starts[1] == 1);
        assertTrue(counts[1] == 1);
        called[2] = false;

        // test prepopulated adapter inserted
        MergedAdapter1 adapter3 = new MergedAdapter1();
        adapter3.add("Test");
        adapter3.add("Another");
        mergedUniversalAdapter.addAdapter(adapter3);
        assertTrue(called[1]);
        assertTrue(starts[1] == 2);
        assertTrue(counts[1] == 2);
        called[1] = false;

        // test all 3 adapters to ensure changing data also changes correctly
        adapter1.set(0, "Test2");
        assertTrue(called[0]);
        assertTrue(starts[0] == 0);
        assertTrue(counts[0] == 1);
        called[0] = false;

        adapter2.set(0, "Test2");
        assertTrue(called[0]);
        assertTrue(starts[0] == 1);
        assertTrue(counts[0] == 1);
        called[0] = false;

        adapter3.set(0, "Test2");
        assertTrue(called[0]);
        assertTrue(starts[0] == 2);
        assertTrue(counts[0] == 1);
        called[0] = false;
    }
}
