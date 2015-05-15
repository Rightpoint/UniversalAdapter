package com.raizlabs.universaladapter.test;

import android.view.View;
import android.widget.LinearLayout;

import com.raizlabs.universaladapter.ViewHolder;
import com.raizlabs.universaladapter.converter.UniversalAdapterTestCase;

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

}
