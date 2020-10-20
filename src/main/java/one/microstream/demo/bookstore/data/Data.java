
package one.microstream.demo.bookstore.data;

public class Data
{
	private final Books     books     = new Books();
	private final Shops     shops     = new Shops();
	private final Customers customers = new Customers();
	private final Purchases purchases = new Purchases();

	public Data()
	{
		super();
	}

	public Books books()
	{
		return this.books;
	}

	public Shops shops()
	{
		return this.shops;
	}

	public Customers customers()
	{
		return this.customers;
	}

	public Purchases purchases()
	{
		return this.purchases;
	}

}
