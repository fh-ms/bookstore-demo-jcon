package one.microstream.demo.bookstore.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.persistence.types.Persister;

public class Shops
{
	private final List<Shop> shops = new ArrayList<>(1024);

	public Shops()
	{
		super();
	}
	
	public void add(final Shop shop)
	{
		this.add(shop, BookStoreDemo.storageManager());
	}
	
	public void add(
		final Shop shop,
		final Persister persister
	)
	{
		this.shops.add(shop);
		persister.store(this.shops);
	}
	
	public void addAll(final Collection<? extends Shop> shops)
	{
		this.addAll(shops, BookStoreDemo.storageManager());
	}
	
	public void addAll(
		final Collection<? extends Shop> shops,
		final Persister persister
	)
	{
		this.shops.addAll(shops);
		persister.store(this.shops);
	}

	public int shopCount()
	{
		return this.shops.size();
	}

	public List<Shop> all()
	{
		return new ArrayList<>(this.shops);
	}
	
	public void clear()
	{
		this.shops.forEach(Shop::clear);
	}
	
	public <T> T compute(
		final Function<Stream<Shop>, T> streamFunction
	)
	{
		return streamFunction.apply(
			this.shops.parallelStream()
		);
	}

	public <T> T computeInventory(
		final Function<Stream<InventoryItem>, T> function
	)
	{
		return function.apply(
			this.shops.parallelStream().flatMap(shop ->
				shop.inventory().compute(entries ->
					entries.map(entry -> new InventoryItem(shop, entry.getKey(), entry.getValue()))
				)
			)
		);
	}

	public Shop ofName(final String name)
	{
		return this.shops.stream()
			.filter(shop -> shop.name().equals(name))
			.findAny()
			.orElse(null)
		;
	}

}
