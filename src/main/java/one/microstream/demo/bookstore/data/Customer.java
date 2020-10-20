
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;
import static one.microstream.demo.bookstore.util.ValidationUtils.requireNonBlank;
import static one.microstream.demo.bookstore.util.ValidationUtils.requirePositive;

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
		this.name       = requireNonBlank(name,       () -> "Name cannot be empty"        );
		this.address    = requireNonNull (address,    () -> "Address cannot be null"      );
		this.customerId = requirePositive(customerId, () -> "Customer id must be positive");
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
