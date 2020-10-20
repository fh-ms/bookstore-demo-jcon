
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;
import static one.microstream.demo.bookstore.util.ValidationUtils.requireNonBlank;

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
		this.name = requireNonBlank(name, () -> "Name cannot be empty");
		this.code = requireNonNull(code,  () -> "Code cannot be null" );
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
