
package one.microstream.demo.bookstore.data;

public class Genre
{
	private final String name;
	
	public Genre(
		final String name
	)
	{
		super();
		this.name = name;
	}

	public String name()
	{
		return this.name;
	}

	@Override
	public String toString()
	{
		return "Genre [name=" + this.name + "]";
	}

}
