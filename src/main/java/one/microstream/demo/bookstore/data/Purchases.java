
package one.microstream.demo.bookstore.data;

import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Range;

import one.microstream.reference.Lazy;


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

	}

	
	private final Map<Integer, Lazy<YearlyPurchases>> yearlyPurchases = new ConcurrentHashMap<>(32);

	public Purchases()
	{
		super();
	}

	public Range<Integer> years()
	{
		final IntSummaryStatistics summary = this.yearlyPurchases.keySet().stream()
			.mapToInt(Integer::intValue)
			.summaryStatistics();
		return Range.closed(summary.getMin(), summary.getMax());
	}

}
