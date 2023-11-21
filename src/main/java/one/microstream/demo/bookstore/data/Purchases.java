
package one.microstream.demo.bookstore.data;

import static one.microstream.demo.bookstore.util.LazyUtils.clearIfStored;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.serializer.persistence.types.Persister;
import org.eclipse.serializer.reference.Lazy;

import com.google.common.collect.Range;

import one.microstream.demo.bookstore.BookStoreDemo;


public class Purchases
{
	static class YearlyPurchases
	{
		/*
		 * Multiple maps holding references to the purchases, for a faster lookup.
		 */
		final Map<Shop,     Lazy<List<Purchase>>> shopToPurchases     = new HashMap<>(128);
		final Map<Employee, Lazy<List<Purchase>>> employeeToPurchases = new HashMap<>(512);
		final Map<Customer, Lazy<List<Purchase>>> customerToPurchases = new HashMap<>(1024);

		YearlyPurchases()
		{
			super();
		}

		YearlyPurchases add(
			final Purchase purchase,
			final Persister persister
		)
		{
			final List<Object> changedObjects = new ArrayList<>();
			addToMap(this.shopToPurchases,     purchase.shop(),     purchase, changedObjects);
			addToMap(this.employeeToPurchases, purchase.employee(), purchase, changedObjects);
			addToMap(this.customerToPurchases, purchase.customer(), purchase, changedObjects);
			if(persister != null && changedObjects.size() > 0)
			{
				persister.storeAll(changedObjects);
			}
			return this;
		}
		
		private static <K> void addToMap(
			final Map<K, Lazy<List<Purchase>>> map,
			final K key,
			final Purchase purchase,
			final List<Object> changedObjects
		)
		{
			Lazy<List<Purchase>> lazy = map.get(key);
			if(lazy == null)
			{
				final ArrayList<Purchase> list = new ArrayList<>(64);
				list.add(purchase);
				lazy = Lazy.Reference(list);
				map.put(key, lazy);
				changedObjects.add(map);
			}
			else
			{
				final List<Purchase> list = lazy.get();
				list.add(purchase);
				changedObjects.add(list);
			}
		}

		/**
		 * Clears all {@link Lazy} references used by this type
		 */
		void clear()
		{
			clearMap(this.shopToPurchases);
			clearMap(this.employeeToPurchases);
			clearMap(this.customerToPurchases);
		}
		
		private static <K> void clearMap(
			final Map<K, Lazy<List<Purchase>>> map
		)
		{
			map.values().forEach(lazy ->
				clearIfStored(lazy).ifPresent(List::clear)
			);
		}
		
	}

	
	private final Map<Integer, Lazy<YearlyPurchases>> yearlyPurchases = new ConcurrentHashMap<>(32);

	public Purchases()
	{
		super();
	}
	
	Set<Customer> init(
		final int year,
		final List<Purchase> purchases,
		final Persister persister
	)
	{
		final YearlyPurchases yearlyPurchases = new YearlyPurchases();
		purchases.forEach(p -> yearlyPurchases.add(p, null));

		final Lazy<YearlyPurchases> lazy = Lazy.Reference(yearlyPurchases);
		this.yearlyPurchases.put(year, lazy);

		persister.store(this.yearlyPurchases);

		final Set<Customer> customers = new HashSet<>(yearlyPurchases.customerToPurchases.keySet());

		yearlyPurchases.clear();
		lazy.clear();

		return customers;
	}
	
	public void add(final Purchase purchase)
	{
		this.add(purchase, BookStoreDemo.storageManager());
	}
	
	public void add(
		final Purchase purchase,
		final Persister persister
	)
	{
		final Integer year = purchase.timestamp().getYear();
		final Lazy<YearlyPurchases> lazy = this.yearlyPurchases.get(year);
		if(lazy != null)
		{
			lazy.get().add(purchase, persister);
		}
		else
		{
			this.yearlyPurchases.put(
				year,
				Lazy.Reference(
					new YearlyPurchases().add(purchase, null)
				)
			);
			persister.store(this.yearlyPurchases);
		}
	}
	
	public void clear()
	{
		this.yearlyPurchases.keySet().forEach(this::clear);
	}

	public void clear(
		final int year
	)
	{
		clearIfStored(this.yearlyPurchases.get(year))
			.ifPresent(YearlyPurchases::clear);
	}

	public Range<Integer> years()
	{
		final IntSummaryStatistics summary = this.yearlyPurchases.keySet().stream()
			.mapToInt(Integer::intValue)
			.summaryStatistics();
		return Range.closed(summary.getMin(), summary.getMax());
	}

}
