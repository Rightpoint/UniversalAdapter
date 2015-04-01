package com.raizlabs.widget.adapters.viewholderstrategy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Abstract class which is a {@link ViewHolderStrategy} of one (source) type,
 * but uses a {@link ViewHolderStrategy} of a different (converted) type to
 * do the work. This works by having subclasses implement {@link #convert(Object)}
 * which maps the source type to the converted type. This allows easy implementation
 * of a {@link ViewHolderStrategy} of a type which wraps another type that you have
 * a {@link ViewHolderStrategy} for already.
 * <br><br>
 * For example, you may have a set of Dates you want to display as strings and
 * a strategy which displays strings. You can then extend this class with Date
 * as the source type, string as your converted type, and call the constructor
 * with your existing string {@link ViewHolderStrategy}. Then, you simply need
 * to implement {@link #convert(Object)} to take a Date and return the string to
 * display - likely via a DateFormat.
 * 
 * @param <SourceItem> The source type that this {@link ViewHolderStrategyConverter}
 * will be passed, and acts as a {@link ViewHolderStrategy} for.
 * @param <ConvertedItem> The type that this {@link ViewHolderStrategyConverter} converts
 * to, and the existing {@link ViewHolderStrategy} knows how to interpret.
 * @param <Holder> The type of the {@link ViewHolder} that the converted
 * {@link ViewHolderStrategy} uses.
 */
public abstract class ViewHolderStrategyConverter<SourceItem, ConvertedItem, Holder>
implements ViewHolderStrategy<SourceItem, Holder> {
	
	ViewHolderStrategy<ConvertedItem, Holder> convertedStrategy;
	
	public ViewHolderStrategyConverter(ViewHolderStrategy<ConvertedItem, Holder> convertedTypeStrategy) {
		this.convertedStrategy = convertedTypeStrategy;
	}

	/**
	 * Converts the source item into the converted type for the converted
	 * {@link ViewHolderStrategy} to use.
	 * @param item The item to convert.
	 * @return The converted object to be passed to the converted {@link ViewHolderStrategy}.
	 */
	protected abstract ConvertedItem convert(SourceItem item);

	@Override
	public boolean areAllItemsEnabled() {
		return convertedStrategy.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(SourceItem item) {
		return convertedStrategy.isEnabled(convert(item));
	}

	@Override
	public int getItemViewType(SourceItem item) {
		return convertedStrategy.getItemViewType(convert(item));
	}

	@Override
	public int getViewTypeCount() {
		return convertedStrategy.getViewTypeCount();
	}

	@Override
	public View inflateView(SourceItem item, LayoutInflater inflater, ViewGroup root) {
		return convertedStrategy.inflateView(convert(item), inflater, root);
	}

	@Override
	public Holder createHolder(SourceItem item) {
		return convertedStrategy.createHolder(convert(item));
	}

	@Override
	public void populateHolder(View view, Holder holder) {
		convertedStrategy.populateHolder(view, holder);
	}

	@Override
	public void updateView(SourceItem item, Holder holder) {
		convertedStrategy.updateView(convert(item), holder);
	}
}
