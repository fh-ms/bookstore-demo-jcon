package one.microstream.demo.bookstore.data;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.persistence.types.Persister;

import one.microstream.demo.bookstore.util.concurrent.ReadWriteLocked;
import one.microstream.gigamap.Condition;
import one.microstream.gigamap.GigaMap;
import one.microstream.gigamap.GigaQuery;

public class Shops extends ReadWriteLocked
{
	private final GigaMap<Shop> map = GigaMap.<Shop>Builder()
		.withBitmapIndex(Named.nameIndex)
		.withBitmapIndex(HasAddress.address1Index)
		.withBitmapIndex(HasAddress.address2Index)
		.withBitmapIndex(HasAddress.zipcodeIndex)
		.withBitmapIndex(HasAddress.cityIndex)
		.withBitmapIndex(HasAddress.stateIndex)
		.withBitmapIndex(HasAddress.countryIndex)
		.build()
	;
	

	public Shops()
	{
		super();
	}
	
	public void add(final Shop shop)
	{
		this.write(() ->
		{
			this.map.add(shop);
			this.map.store();
		});
	}
	
	public void add(
		final Shop shop,
		final Persister persister
	)
	{
		this.write(() ->
		{
			this.map.add(shop);
			persister.store(this.map);
		});
	}
	
	public void addAll(final Collection<? extends Shop> shops)
	{
		this.write(() ->
		{
			this.map.addAll(shops);
			this.map.store();
		});
	}
	
	public void addAll(
		final Collection<? extends Shop> shops,
		final Persister persister
	)
	{
		this.write(() ->
		{
			this.map.addAll(shops);
			persister.store(this.map);
		});
	}

	public long shopCount()
	{
		return this.read(() ->
			this.map.size()
		);
	}

	public void clear()
	{
		this.write(() ->
			this.map.forEach(Shop::clear)
		);
	}

	public Shop ofName(final String name)
	{
		return this.read(() ->
			this.map.query(Named.nameIndex.is(name))
				.findFirst()
				.orElse(null)
		);
	}
	
	public Country countryByCode(final String code)
	{
		return this.read(() ->
			HasAddress.countryIndex.resolveKeys(this.map)
				.stream()
				.filter(c -> c.code().equals(code))
				.findAny()
				.orElse(null)
		);
	}
	
	public <R> R compute(
		final Condition<Shop>           condition,
		final int                       offset,
		final int                       limit,
		final Function<Stream<Shop>, R> function
	)
	{
		return this.read(() ->
		{
			GigaQuery<Shop> query = this.map.query();
			if(condition != null)
			{
				query = query.and(condition);
			}
			return function.apply(query.toList(offset, limit).stream());
		});
	}

}
