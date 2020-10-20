
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;
import static one.microstream.demo.bookstore.util.ValidationUtils.requireZeroOrPositive;

public class BookSales implements Comparable<BookSales>
{
	private final Book book;
	private final int  amount;

	public BookSales(
		final Book book,
		final int amount
	)
	{
		super();
		this.book   = requireNonNull(book,          () -> "Book cannot be null"      );
		this.amount = requireZeroOrPositive(amount, () -> "Amount cannot be negative");
	}

	public Book book()
	{
		return this.book;
	}

	public int amount()
	{
		return this.amount;
	}
	
	@Override
	public int compareTo(
		final BookSales other
	)
	{
		return Integer.compare(other.amount(), this.amount());
	}

	@Override
	public String toString()
	{
		return "BookSales [book=" + this.book + ", amount=" + this.amount + "]";
	}

}
