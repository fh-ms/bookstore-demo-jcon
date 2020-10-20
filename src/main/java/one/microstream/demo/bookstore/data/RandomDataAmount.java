
package one.microstream.demo.bookstore.data;

public class RandomDataAmount
{
	public static RandomDataAmount Minimal()
	{
		return new RandomDataAmount(
			1.0, // minRatio
			1, // maxGenres
			1, // maxCountries
			1, // maxPublishersPerCountry
			1, // maxAuthorsPerCountry
			1, // maxBooksPerCountry
			1, // maxCitiesPerCountry
			1, // maxCustomersPerCity
			1, // maxShopsPerCity
			1, // maxBooksPerShop
			1, // maxAgeOfShopsInYears
			1, // maxEmployeesPerShop
			1, // maxPurchasesPerEmployeePerYear
			1 // maxItemsPerPurchase
		);
	}

	public static RandomDataAmount Small()
	{
		return new RandomDataAmount(
			0.0, // minRatio
			10, // maxGenres
			1, // maxCountries
			10, // maxPublishersPerCountry
			50, // maxAuthorsPerCountry
			100, // maxBooksPerCountry
			10, // maxCitiesPerCountry
			100, // maxCustomersPerCity
			3, // maxShopsPerCity
			250, // maxBooksPerShop
			10, // maxAgeOfShopsInYears
			5, // maxEmployeesPerShop
			100, // maxPurchasesPerEmployeePerYear
			3 // maxItemsPerPurchase
		);
	}

	public static RandomDataAmount Medium()
	{
		return new RandomDataAmount(
			0.3, // minRatio
			100, // maxGenres
			3, // maxCountries
			25, // maxPublishersPerCountry
			250, // maxAuthorsPerCountry
			500, // maxBooksPerCountry
			10, // maxCitiesPerCountry
			500, // maxCustomersPerCity
			4, // maxShopsPerCity
			350, // maxBooksPerShop
			15, // maxAgeOfShopsInYears
			7, // maxEmployeesPerShop
			150, // maxPurchasesPerEmployeePerYear
			3 // maxItemsPerPurchase
		);
	}

	public static RandomDataAmount Large()
	{
		return new RandomDataAmount(
			0.4, // minRatio
			500, // maxGenres
			5, // maxCountries
			50, // maxPublishersPerCountry
			500, // maxAuthorsPerCountry
			1000, // maxBooksPerCountry
			25, // maxCitiesPerCountry
			750, // maxCustomersPerCity
			5, // maxShopsPerCity
			500, // maxBooksPerShop
			20, // maxAgeOfShopsInYears
			10, // maxEmployeesPerShop
			150, // maxPurchasesPerEmployeePerYear
			3 // maxItemsPerPurchase
		);
	}

	public static RandomDataAmount Humongous()
	{
		return new RandomDataAmount(
			0.5, // minRatio
			500, // maxGenres
			5, // maxCountries
			50, // maxPublishersPerCountry
			500, // maxAuthorsPerCountry
			1000, // maxBooksPerCountry
			50, // maxCitiesPerCountry
			750, // maxCustomersPerCity
			5, // maxShopsPerCity
			500, // maxBooksPerShop
			100, // maxAgeOfShopsInYears
			10, // maxEmployeesPerShop
			150, // maxPurchasesPerEmployeePerYear
			3 // maxItemsPerPurchase
		);
	}

	
	private final double minRatio;
	private final int    maxGenres;
	private final int    maxCountries;
	private final int    maxPublishersPerCountry;
	private final int    maxAuthorsPerCountry;
	private final int    maxBooksPerCountry;
	private final int    maxCitiesPerCountry;
	private final int    maxCustomersPerCity;
	private final int    maxShopsPerCity;
	private final int    maxBooksPerShop;
	private final int    maxAgeOfShopsInYears;
	private final int    maxEmployeesPerShop;
	private final int    maxPurchasesPerEmployeePerYear;
	private final int    maxItemsPerPurchase;

	public RandomDataAmount(
		final double minRatio,
		final int maxGenres,
		final int maxCountries,
		final int maxPublishersPerCountry,
		final int maxAuthorsPerCountry,
		final int maxBooksPerCountry,
		final int maxCitiesPerCountry,
		final int maxCustomersPerCity,
		final int maxShopsPerCity,
		final int maxBooksPerShop,
		final int maxAgeOfShopsInYears,
		final int maxEmployeesPerShop,
		final int maxPurchasesPerEmployeePerYear,
		final int maxItemsPerPurchase
	)
	{
		super();
		this.minRatio                       = minRatio;
		this.maxGenres                      = maxGenres;
		this.maxCountries                   = maxCountries;
		this.maxPublishersPerCountry        = maxPublishersPerCountry;
		this.maxAuthorsPerCountry           = maxAuthorsPerCountry;
		this.maxBooksPerCountry             = maxBooksPerCountry;
		this.maxCitiesPerCountry            = maxCitiesPerCountry;
		this.maxCustomersPerCity            = maxCustomersPerCity;
		this.maxShopsPerCity                = maxShopsPerCity;
		this.maxBooksPerShop                = maxBooksPerShop;
		this.maxAgeOfShopsInYears           = maxAgeOfShopsInYears;
		this.maxEmployeesPerShop            = maxEmployeesPerShop;
		this.maxPurchasesPerEmployeePerYear = maxPurchasesPerEmployeePerYear;
		this.maxItemsPerPurchase            = maxItemsPerPurchase;
	}

	public double minRatio()
	{
		return this.minRatio;
	}

	public int maxGenres()
	{
		return this.maxGenres;
	}

	public int maxCountries()
	{
		return this.maxCountries;
	}

	public int maxPublishersPerCountry()
	{
		return this.maxPublishersPerCountry;
	}

	public int maxAuthorsPerCountry()
	{
		return this.maxAuthorsPerCountry;
	}

	public int maxBooksPerCountry()
	{
		return this.maxBooksPerCountry;
	}

	public int maxCitiesPerCountry()
	{
		return this.maxCitiesPerCountry;
	}

	public int maxCustomersPerCity()
	{
		return this.maxCustomersPerCity;
	}

	public int maxShopsPerCity()
	{
		return this.maxShopsPerCity;
	}

	public int maxBooksPerShop()
	{
		return this.maxBooksPerShop;
	}

	public int maxAgeOfShopsInYears()
	{
		return this.maxAgeOfShopsInYears;
	}

	public int maxEmployeesPerShop()
	{
		return this.maxEmployeesPerShop;
	}

	public int maxPurchasesPerEmployeePerYear()
	{
		return this.maxPurchasesPerEmployeePerYear;
	}

	public int maxItemsPerPurchase()
	{
		return this.maxItemsPerPurchase;
	}

}
