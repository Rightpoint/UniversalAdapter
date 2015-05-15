[![Raizlabs Repository](http://img.shields.io/badge/Raizlabs%20Repository-2.0.1-blue.svg?style=flat)](https://github.com/Raizlabs/maven-releases)
# UniversalAdapter

A single adapter implementation for any scrolling view or `ViewGroup`.

This library consolidates the differences between `BaseAdapter`, `RecyclerView.Adapter`, `PagerAdapter`, and binding to `ViewGroup` into one unified API.

Its underlying implementation is based on the `ViewHolder` pattern and abstracts it to work with all of the adapter views.

## Including in your project

### Gradle

Add the maven repo url to your root build.gradle in the ```buildscript{}``` and ```allProjects{}``` blocks:

```groovy

  buildscript {
    repositories {
        maven { url "https://raw.github.com/Raizlabs/maven-releases/master/releases" }
    }
    dependencies {
      classpath 'com.raizlabs:Griddle:1.0.3'
    }
  }

  allprojects {
    repositories {
        maven { url "https://raw.github.com/Raizlabs/maven-releases/master/releases" }
    }
  }


```

Add the library to the project-level build.gradle, using the [Griddle](https://github.com/Raizlabs/Griddle) plugin to simplify your build.gradle and link sources:

```groovy

  apply plugin: 'com.raizlabs.griddle'

  dependencies {
    mod 'com.raizlabs.android:UniversalAdapter:2.0.1'
  }

```

or by standard Gradle use (without linking sources support):

```groovy

  dependencies {
    compile "com.raizlabs.android:UniversalAdapter:2.0.1"
  }

```

## Usage

This library comes packed with some notable features:
  1. Unified adapter binding
  2. List observability: when an adapter's inner content changes, notifications to the parent adapter view happen automatically
  3. Merged adapter: add an arbitrary amount of `UniversalAdapter`'s together to enable diverse view-data sets!
  4. Unified header and footer support: add an arbitrary number of headers and footers to any `UniversalAdapter` and let the library handle it for you, no matter the parent view!
  5. `ViewGroup` binding for views such as `LinearLayout` or `GridLayout`!


### Define an adapter

This library comes with two main adapter classes: `UniversalAdapter` and `ListBasedAdapter`.

`UniversalAdapter` is similar to `BaseAdapter` while not relying on subclassing it.

`ListBasedAdapter` supports a `List` of items. _Note: use ObservableListWrapper to pass changes from this list to this adapter automatically!_

To define an adapter:

```java


ListBasedAdapter<Item, Holder> adapter = new ListBasedAdapter<>() {
  @Override
  protected void onBindViewHolder(Holder viewHolder, Item item, int position) {
    // bind here
  }

  @Override
  protected Holder onCreateViewHolder(ViewGroup parent, int itemType) {
      return new Holder(inflateView(parent, R.layout.my_layout));
  }

}


```

To add items to an adapter:


```java

adapter.loadItemsList(myItems);


```

Now to connect it to a `ListView`, `RecyclerView`, `ViewPager`, or `ViewGroup`:

```java

UniversalConverter<Item, Holder> universalConverter = UniversalConverterFactory.create(adapter, someViewGroup);

// each UniversalConverter determines how the click events works
// you just worry about the callback!
universalConverter.setItemClickedListener(new ItemClickedListener<>() {

  @Override
  public void onItemClicked(UniversalAdapter<Item, Holder> adapter, Item item, Holder holder, int position) {
    // do something here
  }

})

```

This method "converts" the `UniversalAdapter` to the appropriate adapter for the `ViewGroup` passed. If it cannot find a more specific adapter, it utilizes a `ViewGroupAdapter`, which adds all views from the adapter to a `ViewGroup`.

### Merged Adapter

A `MergedUniversalAdapter` allows you to add multiple `UniversalAdapter` and display them all together!

```java

MergedUniversalAdapter merged = new MergedUniversalAdapter();
merged.addAdapter(listAdapter);
merged.addAdapter(anotherAdapter);

// bind the adapter to the ViewGroup you want to use.
UniversalConverterFactory.create(merged, viewGroup);

```

### Header and Footers

One of the pain points of `RecyclerView` is that it does _not_ natively support header and footer views. Also, `ListView` does, but does it internally. We add header and footer support to `UniversalAdapter` to merge the two concepts together at the `Adapter` level.

To use it:

```java

ListBasedAdapter<Item, Holder adapter = new ListBasedAdapter<>();

// add items you want to display
adapter.loadItemList(someList);

// now we want some headers, no problem!
adapter.addHeaderView(myHeaderView);

// we can also add ViewHolders too!
adapter.addHeaderHolder(myViewHolder);

// same for footers
adapter.addFooterView(myFooter);
adapter.addFooterHolder(myFooterHolder);

// bind to ViewGroup of your choice:
UniversalConverterFactory.create(adapter, viewGroup);

```

### ViewGroup adapter

Sometimes we want to simply add a homogenous set of data to a `LinearLayout` or parent `ViewGroup` and don't want to or can't use a `ListView`. Instead of having to create a `BaseAdapter`, write methods to bind the views to the `ViewGroup`, and handle changes to the adapter, we wrote `ViewGroupAdapter`.

You don't even need to worry about it because its the same interface for `ListView`, `RecyclerView`, and `ViewPager`.

```java

LinearLayout layout = (LinearLayout) findViewById(R.id.my_list);

// ties the adapter to this layout
// any changes to the adapter will refresh the content on this layout
UniversalConverterFactory.create(myListAdapter, layout);

```

That's it!
