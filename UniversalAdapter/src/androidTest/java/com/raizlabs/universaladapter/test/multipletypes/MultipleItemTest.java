package com.raizlabs.universaladapter.test.multipletypes;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.raizlabs.universaladapter.converter.UniversalAdapter;
import com.raizlabs.universaladapter.converter.UniversalAdapterTestCase;
import com.raizlabs.universaladapter.test.MultipleItemTypeAdapter;
import com.raizlabs.universaladapter.test.SimpleHeaderHolder;

/**
 * Description: Test multiple item types
 */
public class MultipleItemTest extends UniversalAdapterTestCase {

    public void testMultipleItemTypes() {

        LinearLayout dummyLinear = new LinearLayout(getContext());

        MultipleItemTypeAdapter multipleItemTypeAdapter = new MultipleItemTypeAdapter();
        multipleItemTypeAdapter.add("Multiple");
        multipleItemTypeAdapter.add(5);

        SimpleHeaderHolder headerHolder = new SimpleHeaderHolder(new View(dummyLinear.getContext()));
        multipleItemTypeAdapter.addHeaderHolder(headerHolder);

        assertTotalCount(3, multipleItemTypeAdapter);

        // ensure header is proper viewtype
        assertInternalHolderCreatedType(0, multipleItemTypeAdapter, SimpleHeaderHolder.class, dummyLinear);

        // ensure items view types internally and externally are correct for headers.
        assertHolderCreatedTypeItemCorrectly(MultipleItemTypeAdapter.TYPE_1, multipleItemTypeAdapter,
                                             MultipleItemTypeAdapter.ViewHolder1.class, dummyLinear);
        assertHolderCreatedTypeItemCorrectly(MultipleItemTypeAdapter.TYPE_2, multipleItemTypeAdapter,
                                             MultipleItemTypeAdapter.ViewHolder2.class, dummyLinear);

        // ensure items get Item view type is reported appropriately in the API
        assertItemTypeWasCorrectlySpecified(MultipleItemTypeAdapter.TYPE_1, 0, multipleItemTypeAdapter);
        assertItemTypeWasCorrectlySpecified(MultipleItemTypeAdapter.TYPE_2, 1, multipleItemTypeAdapter);
    }

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
}
