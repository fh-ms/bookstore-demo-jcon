
package one.microstream.demo.bookstore.data;

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
		this.name    = name;
		this.country = country;
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
