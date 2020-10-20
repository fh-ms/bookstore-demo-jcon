
package one.microstream.demo.bookstore.data;

public class Customer
{
	private final int     customerId;
	private final String  name;
	private final Address address;

	public Customer(
		final int     customerId,
		final String  name,
		final Address address
	)
	{
		super();
		this.name       = name;
		this.address    = address;
		this.customerId = customerId;
	}

	public int customerId()
	{
		return this.customerId;
	}

	public String name()
	{
		return this.name;
	}

	public Address address()
	{
		return this.address;
	}

	@Override
	public String toString()
	{
		return "Customer [customerId=" + this.customerId
			+ ", name=" + this.name
			+ ", address=" + this.address + "]"
		;
	}

}
