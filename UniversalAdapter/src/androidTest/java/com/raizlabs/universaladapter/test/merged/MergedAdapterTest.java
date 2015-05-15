package com.raizlabs.universaladapter.test.merged;

import com.raizlabs.universaladapter.converter.MergedUniversalAdapter;
import com.raizlabs.universaladapter.converter.UniversalAdapterTestCase;
import com.raizlabs.universaladapter.test.MultipleItemTypeAdapter;

/**
 * Description: Tests merge adapters with all kinds of crazy adapters inside.
 */
public class MergedAdapterTest extends UniversalAdapterTestCase {


    public void testMergedAdapter() {
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

    }
}
