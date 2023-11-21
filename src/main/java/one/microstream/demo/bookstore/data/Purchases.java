
package one.microstream.demo.bookstore.data;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;
import static one.microstream.demo.bookstore.util.CollectionUtils.ensureParallelStream;
import static one.microstream.demo.bookstore.util.CollectionUtils.maxKey;
import static one.microstream.demo.bookstore.util.CollectionUtils.summingMonetaryAmount;
import static one.microstream.demo.bookstore.util.LazyUtils.clearIfStored;
import static org.javamoney.moneta.function.MonetaryFunctions.summarizingMonetary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.money.MonetaryAmount;

import org.eclipse.serializer.persistence.types.Persister;
import org.eclipse.serializer.reference.Lazy;

import com.google.common.collect.Range;

import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.util.concurrent.ReadWriteLockedStriped;


public class Purchases extends ReadWriteLockedStriped
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
		
		Stream<Purchase> byShop(
			final Shop shop
		)
		{
			return ensureParallelStream(
				Lazy.get(this.shopToPurchases.get(shop))
			);
		}

		Stream<Purchase> byShops(
			final Predicate<Shop> shopSelector
		)
		{
			return this.shopToPurchases.entrySet().parallelStream()
				.filter(e -> shopSelector.test(e.getKey()))
				.flatMap(e -> ensureParallelStream(Lazy.get(e.getValue())));
		}

		Stream<Purchase> byEmployee(
			final Employee employee
		)
		{
			return ensureParallelStream(
				Lazy.get(this.employeeToPurchases.get(employee))
			);
		}

		Stream<Purchase> byCustomer(
			final Customer customer
		)
		{
			return ensureParallelStream(
				Lazy.get(this.customerToPurchases.get(customer))
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
		return this.write(year, () ->
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
		});
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
		this.write(year, () ->
		{
			final Lazy<YearlyPurchases> lazy = this.yearlyPurchases.get(year);
			if(lazy != null)
			{
				lazy.get().add(purchase, persister);
			}
			else
			{
				this.write(0, () -> {
					this.yearlyPurchases.put(
						year,
						Lazy.Reference(
							new YearlyPurchases().add(purchase, null)
						)
					);
					persister.store(this.yearlyPurchases);
				});
			}
		});
	}
	
	public void clear()
	{
		this.yearlyPurchases.keySet().forEach(this::clear);
	}

	public void clear(
		final int year
	)
	{
		this.write(year, () ->
			clearIfStored(this.yearlyPurchases.get(year))
				.ifPresent(YearlyPurchases::clear)
		);
	}

	public Range<Integer> years()
	{
		return this.read(0, () -> {
			final IntSummaryStatistics summary = this.yearlyPurchases.keySet().stream()
				.mapToInt(Integer::intValue)
				.summaryStatistics();
			return Range.closed(summary.getMin(), summary.getMax());
		});
	}

	public <T> T computeByYear(
		final int year,
		final Function<Stream<Purchase>, T> streamFunction
	)
	{
		return this.read(year, () ->
		{
			final YearlyPurchases yearlyPurchases = Lazy.get(this.yearlyPurchases.get(year));
			return streamFunction.apply(
				yearlyPurchases == null
					? Stream.empty()
					: yearlyPurchases.shopToPurchases.values().parallelStream()
						.map(l -> l.get())
						.flatMap(List::stream)
			);
		});
	}

	public <T> T computeByShopAndYear(
		final Shop shop,
		final int year,
		final Function<Stream<Purchase>, T> streamFunction
	)
	{
		return this.read(year, () ->
		{
			final YearlyPurchases yearlyPurchases = Lazy.get(this.yearlyPurchases.get(year));
			return streamFunction.apply(
				yearlyPurchases == null
					? Stream.empty()
					: yearlyPurchases.byShop(shop)
			);
		});
	}

	public <T> T computeByShopsAndYear(
		final Predicate<Shop> shopSelector,
		final int year,
		final Function<Stream<Purchase>, T> streamFunction
	)
	{
		return this.read(year, () ->
		{
			final YearlyPurchases yearlyPurchases = Lazy.get(this.yearlyPurchases.get(year));
			return streamFunction.apply(
				yearlyPurchases == null
					? Stream.empty()
					: yearlyPurchases.byShops(shopSelector)
			);
		});
	}

	public <T> T computeByEmployeeAndYear(
		final Employee employee,
		final int year,
		final Function<Stream<Purchase>, T> streamFunction
	)
	{
		return this.read(year, () ->
		{
			final YearlyPurchases yearlyPurchases = Lazy.get(this.yearlyPurchases.get(year));
			return streamFunction.apply(
				yearlyPurchases == null
					? Stream.empty()
					: yearlyPurchases.byEmployee(employee)
			);
		});
	}

	public <T> T computeByCustomerAndYear(
		final Customer customer,
		final int year,
		final Function<Stream<Purchase>, T> streamFunction
	)
	{
		return this.read(year, () ->
		{
			final YearlyPurchases yearlyPurchases = Lazy.get(this.yearlyPurchases.get(year));
			return streamFunction.apply(
				yearlyPurchases == null
					? Stream.empty()
					: yearlyPurchases.byCustomer(customer)
			);
		});
	}

	public List<BookSales> bestSellerList(
		final int year
	)
	{
		return this.computeByYear(
			year,
			this::bestSellerList
		);
	}

	public List<BookSales> bestSellerList(
		final int year,
		final Country country
	)
	{
		return this.computeByShopsAndYear(
			shopInCountryPredicate(country),
			year,
			this::bestSellerList
		);
	}

	private List<BookSales> bestSellerList(
		final Stream<Purchase> purchases
	)
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

	public long countPurchasesOfForeigners(
		final int year
	)
	{
		return this.computePurchasesOfForeigners(
			year,
			purchases -> purchases.count()
		);
	}

	public List<Purchase> purchasesOfForeigners(
		final int year
	)
	{
		return this.computePurchasesOfForeigners(
			year,
			purchases -> purchases.collect(toList())
		);
	}

	private <T> T computePurchasesOfForeigners(
		final int year,
		final Function <Stream<Purchase>, T> streamFunction
	)
	{
		return this.computeByYear(
			year,
			purchases -> streamFunction.apply(
				purchases.filter(
					purchaseOfForeignerPredicate()
				)
			)
		);
	}

	public long countPurchasesOfForeigners(
		final int year,
		final Country country
	)
	{
		return this.computePurchasesOfForeigners(
			year,
			country,
			purchases -> purchases.count()
		);
	}

	public List<Purchase> purchasesOfForeigners(
		final int year,
		final Country country
	)
	{
		return this.computePurchasesOfForeigners(
			year,
			country,
			purchases -> purchases.collect(toList())
		);
	}

	private <T> T computePurchasesOfForeigners(
		final int year,
		final Country country,
		final Function <Stream<Purchase>, T> streamFunction
	)
	{
		return this.computeByShopsAndYear(
			shopInCountryPredicate(country),
			year,
			purchases -> streamFunction.apply(
				purchases.filter(
					purchaseOfForeignerPredicate()
				)
			)
		);
	}

	private static Predicate<Shop> shopInCountryPredicate(final Country country)
	{
		return shop -> shop.address().city().state().country() == country;
	}

	private static Predicate<? super Purchase> purchaseOfForeignerPredicate()
	{
		return p -> p.customer().address().city() != p.shop().address().city();
	}

	public MonetaryAmount revenueOfShopInYear(
		final Shop shop,
		final int year
	)
	{
		return this.computeByShopAndYear(
			shop,
			year,
			purchases -> purchases
				.map(Purchase::total)
				.collect(summarizingMonetary(BookStoreDemo.currencyUnit()))
				.getSum()
		);
	}

	public Employee employeeOfTheYear(
		final int year
	)
	{
		return this.computeByYear(
			year,
			bestPerformingEmployeeFunction()
		);
	}

	public Employee employeeOfTheYear(
		final int year,
		final Country country
	)
	{
		return this.computeByShopsAndYear(
			shopInCountryPredicate(country) ,
			year,
			bestPerformingEmployeeFunction()
		);
	}

	private static Function<Stream<Purchase>, Employee> bestPerformingEmployeeFunction()
	{
		return purchases -> maxKey(
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
	
}
