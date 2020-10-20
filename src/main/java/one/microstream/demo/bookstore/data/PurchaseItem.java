package one.microstream.demo.bookstore.data;

import javax.money.MonetaryAmount;


public class PurchaseItem
{
	private final Book           book;
	private final int            amount;
	private final MonetaryAmount price;

	public PurchaseItem(
		final Book book,
		final int  amount
	)
	{
		super();
		this.book   = book;
		this.amount = amount;
		this.price  = book.retailPrice();
	}

	public Book book()
	{
		return this.book;
	}

	public int amount()
	{
		return this.amount;
	}

	public MonetaryAmount price()
	{
		return this.price;
	}

	public MonetaryAmount itemTotal()
	{
		return this.price.multiply(this.amount);
	}

	@Override
	public String toString()
	{
		return "PurchaseItem [book=" + this.book + ", amount=" + this.amount + ", price=" + this.price + "]";
	}
	
	

}