
package one.microstream.demo.bookstore.data;

import java.util.Locale;

public class Language
{
	private final Locale locale;
	
	public Language(
		final Locale locale
	)
	{
		super();
		this.locale = locale;
	}
	
	public Locale locale()
	{
		return this.locale;
	}

	public String name()
	{
		return this.locale.getDisplayName();
	}

	@Override
	public String toString()
	{
		return "Language [locale=" + this.locale + "]";
	}

}
