
package one.microstream.demo.bookstore.data;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;
import static one.microstream.demo.bookstore.util.CollectionUtils.maxKey;
import static one.microstream.demo.bookstore.util.CollectionUtils.summingMonetaryAmount;
import static org.javamoney.moneta.function.MonetaryFunctions.summarizingMonetary;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.money.MonetaryAmount;

import org.eclipse.serializer.persistence.types.Persister;

import com.google.common.collect.Range;

import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.util.concurrent.ReadWriteLocked;
import one.microstream.gigamap.Condition;
import one.microstream.gigamap.GigaMap;
import one.microstream.gigamap.GigaQuery;


public class Purchases extends ReadWriteLocked
{
	private final GigaMap<Purchase> map = GigaMap.<Purchase>Builder()
		.withBitmapIndex(Purchase.yearIndex)
		.withBitmapIndex(Purchase.foreignIndex)
		.withBitmapIndex(Purchase.shopIndex)
		.withBitmapIndex(Purchase.shopCountryIndex)
		.withBitmapIndex(Purchase.employeeIndex)
		.withBitmapIndex(Purchase.customerIndex)
		.build()
	;
	

	public Purchases()
	{
		super();
	}
	
	Set<Customer> init(
		final List<Purchase> purchases,
		final Persister persister
	)
	{
		return this.write(() ->
		{
			this.map.addAll(purchases);
			persister.store(this.map);
			this.map.release();
						
			return purchases.stream().map(p -> p.customer())
				.distinct()
				.collect(Collectors.toSet());
		});
	}
	
	public void add(final Purchase purchase)
	{
		this.write(() ->
		{
			this.map.add(purchase);
			this.map.store();
		});
	}
	
	public void add(
		final Purchase purchase,
		final Persister persister
	)
	{
		this.write(() ->
		{
			this.map.add(purchase);
			persister.store(this.map);
		});
	}
	
	public Range<Integer> years()
	{
		return this.read(() -> {
			final IntSummaryStatistics summary = new IntSummaryStatistics();
			Purchase.yearIndex.resolveFor(this.map.index().bitmap()).iterateKeys(summary::accept);
			return Range.closed(summary.getMin(), summary.getMax());
		});
	}
	
	public void clear()
	{
		this.map.release();
	}
	
	public List<BookSales> bestSellerList(final int year)
	{
		return this.read(() ->
			this.bestSellerList(
				this.map.query(Purchase.yearIndex.is(year)).stream()
			)
		);
	}
	
	public List<BookSales> bestSellerList(
		final int     year   ,
		final Country country
	)
	{
		return this.read(() ->
			this.bestSellerList(
				this.map.query(Purchase.yearIndex.is(year).and(Purchase.shopCountryIndex.is(country))).stream()
			)
		);
	}
	
	private List<BookSales> bestSellerList(final Stream<Purchase> purchases)
	{
		return purchases
			.flatMap(Purchase::items)
			.collect(
				groupingBy(
					PurchaseItem::book,
					summingInt(PurchaseItem::amount)
				)
			)
			.entrySet()
			.stream()
			.map(e -> new BookSales(e.getKey(), e.getValue()))
			.sorted()
			.collect(toList());
	}
	
	public long countPurchasesOfForeigners(final int year)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year).and(Purchase.foreignIndex.isTrue())).count()
		);
	}

	public List<Purchase> purchasesOfForeigners(final int year)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year).and(Purchase.foreignIndex.isTrue())).toList()
		);
	}
	
	public long countPurchasesOfForeigners(
		final int     year   ,
		final Country country
	)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year)
				.and(Purchase.shopCountryIndex.is(country))
				.and(Purchase.foreignIndex.isTrue())).count()
		);
	}

	public List<Purchase> purchasesOfForeigners(
		final int     year   ,
		final Country country
	)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year)
				.and(Purchase.shopCountryIndex.is(country))
				.and(Purchase.foreignIndex.isTrue())).toList()
		);
	}

	public MonetaryAmount revenueOfShopInYear(
		final Shop shop,
		final int  year
	)
	{
		return this.read(() ->
			this.map.query(Purchase.yearIndex.is(year).and(Purchase.shopIndex.is(shop)))
				.stream()
				.map(Purchase::total)
				.collect(summarizingMonetary(BookStoreDemo.currencyUnit()))
				.getSum()
		);
	}

	public Employee employeeOfTheYear(final int year)
	{
		return this.read(() ->
			bestPerformingEmployee(
				this.map.query(Purchase.yearIndex.is(year)).stream()
			)
		);
	}

	public Employee employeeOfTheYear(
		final int     year   ,
		final Country country
	)
	{
		return this.read(() ->
			bestPerformingEmployee(
				this.map.query(Purchase.yearIndex.is(year).and(Purchase.shopCountryIndex.is(country))).stream()
			)
		);
	}

	private static Employee bestPerformingEmployee(final Stream<Purchase> purchases)
	{
		return maxKey(
			purchases.collect(
				groupingBy(
					Purchase::employee,
					summingMonetaryAmount(
						BookStoreDemo.currencyUnit(),
						Purchase::total
					)
				)
			)
		);
	}
	
	public <R> R compute(
		final Condition<Purchase>           condition,
		final int                           offset,
		final int                           limit,
		final Function<Stream<Purchase>, R> function
	)
	{
		return this.read(() ->
		{
			GigaQuery<Purchase> query = this.map.query();
			if(condition != null)
			{
				query = query.and(condition);
			}
			return function.apply(query.toList(offset, limit).stream());
		});
	}
	
}
