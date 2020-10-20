
package one.microstream.demo.bookstore.data;

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
		this.name  = name;
		this.state = state;
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
