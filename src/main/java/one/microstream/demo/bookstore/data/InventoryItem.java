package one.microstream.demo.bookstore.data;

public class InventoryItem
{
	private final Shop shop;
	private final Book book;
	private final int  amount;

	public InventoryItem(
		final Shop shop,
		final Book book,
		final int amount
	)
	{
		super();
		this.shop   = shop;
		this.book   = book;
		this.amount = amount;
	}

	public Shop shop()
	{
		return this.shop;
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
	public String toString()
	{
		return "InventoryItem [shop=" + this.shop + ", book=" + this.book + ", amount=" + this.amount + "]";
	}

}
