package com.raizlabs.universaladapter.test.multipletypes;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.raizlabs.android.coreutils.util.observable.lists.ListObserver;
import com.raizlabs.android.coreutils.util.observable.lists.ListObserverListener;
import com.raizlabs.universaladapter.converter.UniversalAdapter;
import com.raizlabs.universaladapter.converter.UniversalAdapterTestCase;
import com.raizlabs.universaladapter.test.MultipleItemTypeAdapter;
import com.raizlabs.universaladapter.test.SimpleFooterHolder;
import com.raizlabs.universaladapter.test.SimpleHeaderHolder;

/**
 * Description: Test multiple item types
 */
public class MultipleItemTest extends UniversalAdapterTestCase {

    static void assertHolderCreatedTypeItemCorrectly(int viewType, UniversalAdapter universalAdapter, Class<?> type,
                                                     ViewGroup layout) {
        assertInternalHolderCreatedType(viewType + universalAdapter.getHeadersCount(), universalAdapter, type, layout);
        assertItemHolderCreatedType(viewType, universalAdapter, type, layout);
    }

    static void assertItemTypeWasCorrectlySpecified(int itemType, int position, UniversalAdapter universalAdapter) {
        assertInternalItemViewType(itemType + universalAdapter.getHeadersCount(),
                                   position + universalAdapter.getHeadersCount(), universalAdapter);
        assertExternalItemViewType(itemType, position, universalAdapter);
    }

    public void testMultipleItemTypes() {

        LinearLayout dummyLinear = new LinearLayout(getContext());

        MultipleItemTypeAdapter multipleItemTypeAdapter = new MultipleItemTypeAdapter();
        multipleItemTypeAdapter.add("Multiple");
        multipleItemTypeAdapter.add(5);
        multipleItemTypeAdapter.add(0.5f);
        multipleItemTypeAdapter.add(4);

        SimpleHeaderHolder headerHolder = new SimpleHeaderHolder(new View(dummyLinear.getContext()));
        multipleItemTypeAdapter.addHeaderHolder(headerHolder);

        headerHolder = new SimpleHeaderHolder(new View(dummyLinear.getContext()));
        multipleItemTypeAdapter.addHeaderHolder(headerHolder);

        assertTotalCount(6, multipleItemTypeAdapter);

        SimpleFooterHolder simpleFooterHolder = new SimpleFooterHolder(new View(dummyLinear.getContext()));
        multipleItemTypeAdapter.addFooterHolder(simpleFooterHolder);

        assertTotalCount(7, multipleItemTypeAdapter);

        assertTrue(multipleItemTypeAdapter.get(0) instanceof String);
        assertTrue(multipleItemTypeAdapter.get(1) instanceof Integer);
        assertTrue(multipleItemTypeAdapter.get(2) instanceof Float);
        assertTrue(multipleItemTypeAdapter.get(3) instanceof Integer);

        // ensure header is proper viewtype
        assertInternalHolderCreatedType(0, multipleItemTypeAdapter, SimpleHeaderHolder.class, dummyLinear);
        assertInternalHolderCreatedType(1, multipleItemTypeAdapter, SimpleHeaderHolder.class, dummyLinear);

        // ensure footer is correct too
        assertInternalHolderCreatedType(5, multipleItemTypeAdapter, SimpleFooterHolder.class, dummyLinear);

        // ensure items view types internally and externally are correct for headers.
        assertHolderCreatedTypeItemCorrectly(MultipleItemTypeAdapter.TYPE_1, multipleItemTypeAdapter,
                                             MultipleItemTypeAdapter.ViewHolder1.class, dummyLinear);
        assertHolderCreatedTypeItemCorrectly(MultipleItemTypeAdapter.TYPE_2, multipleItemTypeAdapter,
                                             MultipleItemTypeAdapter.ViewHolder2.class, dummyLinear);
        assertHolderCreatedTypeItemCorrectly(MultipleItemTypeAdapter.TYPE_3, multipleItemTypeAdapter,
                                             MultipleItemTypeAdapter.ViewHolder3.class, dummyLinear);

        // ensure items get Item view type is reported appropriately in the API
        assertItemTypeWasCorrectlySpecified(MultipleItemTypeAdapter.TYPE_1, 0, multipleItemTypeAdapter);
        assertItemTypeWasCorrectlySpecified(MultipleItemTypeAdapter.TYPE_2, 1, multipleItemTypeAdapter);
        assertItemTypeWasCorrectlySpecified(MultipleItemTypeAdapter.TYPE_3, 2, multipleItemTypeAdapter);
        assertItemTypeWasCorrectlySpecified(MultipleItemTypeAdapter.TYPE_2, 3, multipleItemTypeAdapter);

        // ensure we can bind to all items correctly
        for(int i = 0; i < getInternalCount(multipleItemTypeAdapter); i++) {
            assertBindCorrectly(i, dummyLinear, multipleItemTypeAdapter);
        }
    }


    public void testMultipleListeners() {
        LinearLayout dummy = new LinearLayout(getContext());
        MultipleItemTypeAdapter adapter = new MultipleItemTypeAdapter();
        final boolean[] changes = new boolean[4];
        final int[] startPositions = new int[3];
        adapter.getListObserver().addListener(new ListObserverListener<Object>() {
            @Override
            public void onItemRangeChanged(ListObserver<Object> listObserver, int i, int i1) {
                changes[0] = true;
                startPositions[0] = i;
            }

            @Override
            public void onItemRangeInserted(ListObserver<Object> listObserver, int i, int i1) {
                changes[1] = true;
                startPositions[1] = i;
            }

            @Override
            public void onItemRangeRemoved(ListObserver<Object> listObserver, int i, int i1) {
                changes[2] = true;
                startPositions[2] = i;
            }

            @Override
            public void onGenericChange(ListObserver<Object> listObserver) {
                changes[3] = true;

            }
        });

        adapter.add("First");
        assertTrue(changes[1]);
        assertTrue(startPositions[1] == 0);
        changes[1] = false;

        adapter.addHeaderHolder(new SimpleHeaderHolder(new View(dummy.getContext())));
        assertTrue(changes[1]);
        assertTrue(startPositions[1] == 0);
        changes[1] = false;


        adapter.addFooterHolder(new SimpleFooterHolder(new View(dummy.getContext())));
        assertTrue(changes[1]);
        assertTrue(startPositions[1] == 2);
        changes[1] = false;
    }
}
