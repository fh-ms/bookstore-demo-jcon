
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;
import static one.microstream.demo.bookstore.util.ValidationUtils.requireNonEmpty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.money.MonetaryAmount;

import one.microstream.gigamap.Indexer;


public class Purchase
{
	public static final Indexer.AbstractInteger<Purchase> yearIndex = new Indexer.AbstractInteger<>()
	{
		@Override
		public Integer indexEntity(final Purchase entity)
		{
			return entity.timestamp().getYear();
		}
	};
	
	public static final Indexer.AbstractBoolean<Purchase> foreignIndex = new Indexer.AbstractBoolean<>()
	{
		@Override
		public Boolean indexEntity(final Purchase entity)
		{
			return entity.customer().address().city() != entity.shop().address().city();
		}
	};
	
	public static final Indexer.Abstract<Purchase, Shop> shopIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Shop> keyType()
		{
			return Shop.class;
		}
		
		@Override
		public Shop indexEntity(final Purchase entity)
		{
			return entity.shop();
		}
	};
	
	public static final Indexer.Abstract<Purchase, Country> shopCountryIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Country> keyType()
		{
			return Country.class;
		}
		
		@Override
		public Country indexEntity(final Purchase entity)
		{
			return entity.shop().address().city().state().country();
		}
	};
	
	public static final Indexer.Abstract<Purchase, Employee> employeeIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Employee> keyType()
		{
			return Employee.class;
		}
		
		@Override
		public Employee indexEntity(final Purchase entity)
		{
			return entity.employee();
		}
	};
	
	public static final Indexer.Abstract<Purchase, Customer> customerIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Customer> keyType()
		{
			return Customer.class;
		}
		
		@Override
		public Customer indexEntity(final Purchase entity)
		{
			return entity.customer();
		}
	};
	
	
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
		this.shop      = requireNonNull(shop,      () -> "Shop cannot be null"     );
		this.employee  = requireNonNull(employee,  () -> "Employee cannot be null" );
		this.customer  = requireNonNull(customer,  () -> "Customer cannot be null" );
		this.timestamp = requireNonNull(timestamp, () -> "Timestamp cannot be null");
		this.items     = new ArrayList<>(requireNonEmpty(items, () -> "at least one item required in purchase"));
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
