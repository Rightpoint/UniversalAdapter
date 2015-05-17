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

        MergedAdapter1 adapter1 = new MergedAdapter1();
        mergedUniversalAdapter.addAdapter(adapter1);
        assertTrue(called[1]);
        assertTrue(starts[1] == 0);
        assertTrue(counts[1] == 0);
    }
}
