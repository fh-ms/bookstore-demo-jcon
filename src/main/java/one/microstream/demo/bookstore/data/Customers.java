package one.microstream.demo.bookstore.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Customers
{
	private final Map<Integer, Customer> customers = new HashMap<>();

	public Customers()
	{
		super();
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
