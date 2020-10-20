
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;
import static one.microstream.demo.bookstore.util.ValidationUtils.requireNonBlank;

public class Publisher
{
	private final String  name;
	private final Address address;
	
	public Publisher(
		final String  name,
		final Address address
	)
	{
		super();
		this.name    = requireNonBlank(name,    () -> "Name cannot be empty"  );
		this.address = requireNonNull (address, () -> "Address cannot be null");
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
		return "Publisher [name=" + this.name + ", address=" + this.address + "]";
	}

}
