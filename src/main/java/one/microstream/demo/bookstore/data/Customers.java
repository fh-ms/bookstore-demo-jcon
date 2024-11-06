package one.microstream.demo.bookstore.data;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.persistence.types.Persister;

import one.microstream.demo.bookstore.util.concurrent.ReadWriteLocked;
import one.microstream.gigamap.Condition;
import one.microstream.gigamap.GigaMap;
import one.microstream.gigamap.GigaQuery;

public class Customers extends ReadWriteLocked
{
	private final GigaMap<Customer> map = GigaMap.<Customer>Builder()
		.withBitmapIdentityIndex(Customer.idIndex)
		.withBitmapIndex(HasAddress.address1Index)
		.withBitmapIndex(HasAddress.address2Index)
		.withBitmapIndex(HasAddress.zipcodeIndex)
		.withBitmapIndex(HasAddress.cityIndex)
		.withBitmapIndex(HasAddress.stateIndex)
		.withBitmapIndex(HasAddress.countryIndex)
		.withBitmapIndex(Named.nameIndex)
		.build()
	;
	

	public Customers()
	{
		super();
	}
	
	public void add(final Customer customer)
	{
		this.write(() ->
		{
			this.map.add(customer);
			this.map.store();
		});
	}
	
	public void add(
		final Customer customer,
		final Persister persister
	)
	{
		this.write(() ->
		{
			this.map.add(customer);
			persister.store(this.map);
		});
	}

	public void addAll(final Collection<? extends Customer> customers)
	{
		this.write(() ->
		{
			this.map.addAll(customers);
			this.map.store();
		});
	}
	
	public void addAll(
		final Collection<? extends Customer> customers,
		final Persister persister
	)
	{
		this.write(() ->
		{
			this.map.addAll(customers);
			persister.store(this.map);
		});
	}
	
	public long customerCount()
	{
		return this.read(() ->
			this.map.size()
		);
	}

	public Customer ofId(final int customerId)
	{
		return this.read(() ->
			this.map.query(Customer.idIndex.is(customerId)).findFirst().orElse(null)
		);
	}
	
	public <R> R compute(
		final Condition<Customer>           condition,
		final int                           offset,
		final int                           limit,
		final Function<Stream<Customer>, R> function
	)
	{
		return this.read(() ->
		{
			GigaQuery<Customer> query = this.map.query();
			if(condition != null)
			{
				query = query.and(condition);
			}
			return function.apply(query.toList(offset, limit).stream());
		});
	}

}
