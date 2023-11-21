package one.microstream.demo.bookstore.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.persistence.types.Persister;

import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.util.concurrent.ReadWriteLocked;

public class Shops extends ReadWriteLocked
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
		this.write(() -> {
			this.shops.add(shop);
			persister.store(this.shops);
		});
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
		this.write(() -> {
			this.shops.addAll(shops);
			persister.store(this.shops);
		});
	}

	public int shopCount()
	{
		return this.read(
			this.shops::size
		);
	}

	public List<Shop> all()
	{
		return this.read(() ->
			new ArrayList<>(this.shops)
		);
	}
	
	public void clear()
	{
		this.write(() ->
			this.shops.forEach(Shop::clear)
		);
	}

	public <T> T compute(
		final Function<Stream<Shop>, T> streamFunction
	)
	{
		return this.read(() ->
			streamFunction.apply(
				this.shops.parallelStream()
			)
		);
	}

	public <T> T computeInventory(
		final Function<Stream<InventoryItem>, T> function
	)
	{
		return this.read(() ->
			function.apply(
				this.shops.parallelStream().flatMap(shop ->
					shop.inventory().compute(entries ->
						entries.map(entry -> new InventoryItem(shop, entry.getKey(), entry.getValue()))
					)
				)
			)
		);
	}

	public Shop ofName(final String name)
	{
		return this.read(() ->
			this.shops.stream()
				.filter(shop -> shop.name().equals(name))
				.findAny()
				.orElse(null)
		);
	}

}
