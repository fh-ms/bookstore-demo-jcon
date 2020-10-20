
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;
import static one.microstream.demo.bookstore.util.ValidationUtils.requireNonBlank;

public class State
{
	private final String  name;
	private final Country country;

	public State(
		final String name,
		final Country country
	)
	{
		super();
		this.name    = requireNonBlank(name,   () -> "Name cannot be empty"  );
		this.country = requireNonNull(country, () -> "Country cannot be null");
	}

	public String name()
	{
		return this.name;
	}
	
	public Country country()
	{
		return this.country;
	}

	@Override
	public String toString()
	{
		return "State [name=" + this.name + ", country=" + this.country + "]";
	}

}
