package com.raizlabs.universaladapter.converter.listeners;

import com.raizlabs.universaladapter.converter.UniversalAdapter;

/**
 * A unified interface for when selection disappears from an {@link UniversalAdapter}.
 */
public interface NothingSelectedListener {
    /**
     * Called when selection disappears from the adapter.
     * @param adapter The adapter whose selection disappeared.
     */
    void onNothingSelected(UniversalAdapter adapter);
}
