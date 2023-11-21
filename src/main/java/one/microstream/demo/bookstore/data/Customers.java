package one.microstream.demo.bookstore.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.serializer.persistence.types.Persister;

import one.microstream.demo.bookstore.BookStoreDemo;

public class Customers
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
		this.customers.put(customer.customerId(), customer);
		persister.store(this.customers);
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
		this.customers.putAll(
			customers.stream().collect(
				Collectors.toMap(Customer::customerId, Function.identity())
			)
		);
		persister.store(this.customers);
	}
	
	public int customerCount()
	{
		return this.customers.size();
	}

	public List<Customer> all()
	{
		return new ArrayList<>(this.customers.values());
	}

	public Customer ofId(final int customerId)
	{
		return this.customers.get(customerId);
	}

}
