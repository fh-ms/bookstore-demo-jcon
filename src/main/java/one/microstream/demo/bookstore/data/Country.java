
package one.microstream.demo.bookstore.data;

public class Country
{
	private final String name;
	private final String code;
	
	public Country(
		final String name,
		final String code
	)
	{
		super();
		this.name = name;
		this.code = code;
	}

	public String name()
	{
		return this.name;
	}

	public String code()
	{
		return this.code;
	}

	@Override
	public String toString()
	{
		return "Country [name=" + this.name + ", code=" + this.code + "]";
	}

}
