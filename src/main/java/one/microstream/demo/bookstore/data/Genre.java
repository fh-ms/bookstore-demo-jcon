
package one.microstream.demo.bookstore.data;

import static one.microstream.demo.bookstore.util.ValidationUtils.requireNonBlank;

public class Genre implements Named
{
	private final String name;
	
	public Genre(
		final String name
	)
	{
		super();
		this.name = requireNonBlank(name, () -> "Name cannot be empty");
	}

	@Override
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
