
package one.microstream.demo.bookstore.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.money.MonetaryAmount;


public class Purchase
{
	private final Shop               shop;
	private final Employee           employee;
	private final Customer           customer;
	private final LocalDateTime      timestamp;
	private final List<PurchaseItem> items;
	private transient MonetaryAmount total;

	public Purchase(
		final Shop               shop,
		final Employee           employee,
		final Customer           customer,
		final LocalDateTime      timestamp,
		final List<PurchaseItem> items
	)
	{
		super();
		this.shop      = shop;
		this.employee  = employee;
		this.customer  = customer;
		this.timestamp = timestamp;
		this.items     = new ArrayList<>(items);
	}

	public Shop shop()
	{
		return this.shop;
	}

	public Employee employee()
	{
		return this.employee;
	}

	public Customer customer()
	{
		return this.customer;
	}

	public LocalDateTime timestamp()
	{
		return this.timestamp;
	}

	public Stream<PurchaseItem> items()
	{
		return this.items.stream();
	}

	public int itemCount()
	{
		return this.items.size();
	}

	public MonetaryAmount total()
	{
		if(this.total  == null)
		{
			MonetaryAmount total = null;
			for(final PurchaseItem item : this.items)
			{
				total = total == null
					? item.itemTotal()
					: total.add(item.itemTotal());
			}
			this.total = total;
		}
		return this.total;
	}

	@Override
	public String toString()
	{
		return "Purchase [shop=" + this.shop
			+ ", employee=" + this.employee
			+ ", customer=" + this.customer
			+ ", timestamp=" + this.timestamp
			+ ", items=" + this.items + "]"
		;
	}
	
	

}
