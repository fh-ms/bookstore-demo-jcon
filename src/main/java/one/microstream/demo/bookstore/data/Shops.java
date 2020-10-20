package one.microstream.demo.bookstore.data;

import java.util.ArrayList;
import java.util.List;

public class Shops
{
	private final List<Shop> shops = new ArrayList<>(1024);

	public Shops()
	{
		super();
	}

	public int shopCount()
	{
		return this.shops.size();
	}

	public List<Shop> all()
	{
		return new ArrayList<>(this.shops);
	}

}
