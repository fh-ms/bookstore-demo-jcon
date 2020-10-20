
package one.microstream.demo.bookstore.data;

import javax.money.MonetaryAmount;

public class Book
{
	private final String         isbn13;
	private final String         title;
	private final Author         author;
	private final Genre          genre;
	private final Publisher      publisher;
	private final Language       language;
	private final MonetaryAmount purchasePrice;
	private final MonetaryAmount retailPrice;

	public Book(
		final String isbn13,
		final String title,
		final Author author,
		final Genre genre,
		final Publisher publisher,
		final Language language,
		final MonetaryAmount purchasePrice,
		final MonetaryAmount retailPrice
	)
	{
		super();
		this.isbn13        = isbn13;
		this.title         = title;
		this.author        = author;
		this.genre         = genre;
		this.publisher     = publisher;
		this.language      = language;
		this.purchasePrice = purchasePrice;
		this.retailPrice   = retailPrice;
	}

	public String isbn13()
	{
		return this.isbn13;
	}

	public String title()
	{
		return this.title;
	}

	public Author author()
	{
		return this.author;
	}

	public Genre genre()
	{
		return this.genre;
	}

	public Publisher publisher()
	{
		return this.publisher;
	}

	public Language language()
	{
		return this.language;
	}

	public MonetaryAmount purchasePrice()
	{
		return this.purchasePrice;
	}

	public MonetaryAmount retailPrice()
	{
		return this.retailPrice;
	}

	@Override
	public String toString()
	{
		return "Book [isbn13="
			+ this.isbn13
			+ ", title="
			+ this.title
			+ ", author="
			+ this.author
			+ ", genre="
			+ this.genre
			+ ", publisher="
			+ this.publisher
			+ ", language="
			+ this.language
			+ ", purchasePrice="
			+ this.purchasePrice
			+ ", retailPrice="
			+ this.retailPrice
			+ "]";
	}

}
