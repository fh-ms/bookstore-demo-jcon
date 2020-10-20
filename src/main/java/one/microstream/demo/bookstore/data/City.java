
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;
import static one.microstream.demo.bookstore.util.ValidationUtils.requireNonBlank;

public class City
{
	private final String name;
	private final State  state;

	public City(
		final String name,
		final State  state
	)
	{
		super();
		this.name  = requireNonBlank(name, () -> "Name cannot be empty");
		this.state = requireNonNull(state, () -> "State cannot be null");
	}

	public String name()
	{
		return this.name;
	}

	public State state()
	{
		return this.state;
	}

	@Override
	public String toString()
	{
		return "City [name=" + this.name + ", state=" + this.state + "]";
	}

}
