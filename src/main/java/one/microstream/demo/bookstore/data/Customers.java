package one.microstream.demo.bookstore.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.serializer.persistence.types.Persister;

import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.util.concurrent.ReadWriteLocked;

public class Customers extends ReadWriteLocked
{
	private final Map<Integer, Customer> customers = new HashMap<>();

	public Customers()
	{
		super();
	}
	
	public void add(final Customer customer)
	{
		this.add(customer, BookStoreDemo.storageManager());
	}
	
	public void add(
		final Customer customer,
		final Persister persister
	)
	{
		this.write(() -> {
			this.customers.put(customer.customerId(), customer);
			persister.store(this.customers);
		});
	}

	public void addAll(final Collection<? extends Customer> customers)
	{
		this.addAll(customers, BookStoreDemo.storageManager());
	}
	
	public void addAll(
		final Collection<? extends Customer> customers,
		final Persister persister
	)
	{
		this.write(() -> {
			this.customers.putAll(
				customers.stream().collect(
					Collectors.toMap(Customer::customerId, Function.identity())
				)
			);
			persister.store(this.customers);
		});
	}
	
	public int customerCount()
	{
		return this.read(
			this.customers::size
		);
	}

	public List<Customer> all()
	{
		return this.read(() ->
			new ArrayList<>(this.customers.values())
		);
	}

	public Customer ofId(final int customerId)
	{
		return this.read(() ->
			this.customers.get(customerId)
		);
	}
	
	public <T> T compute(
		final Function<Stream<Customer>, T> streamFunction
	)
	{
		return this.read(() ->
			streamFunction.apply(
				this.customers.values().parallelStream()
			)
		);
	}

}
