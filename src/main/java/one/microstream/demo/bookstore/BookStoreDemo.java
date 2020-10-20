
package one.microstream.demo.bookstore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.format.AmountFormatQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.RoundedMoney;
import org.javamoney.moneta.format.CurrencyStyle;

import one.microstream.demo.bookstore.data.Data;
import one.microstream.demo.bookstore.data.RandomDataAmount;
import one.microstream.demo.bookstore.data.RandomDataGenerator;
import one.microstream.jdk8.java.util.BinaryHandlersJDK8;
import one.microstream.storage.configuration.Configuration;
import one.microstream.storage.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.EmbeddedStorageManager;


public final class BookStoreDemo
{
	private static final CurrencyUnit         CURRENCY_UNIT          = Monetary.getCurrency(Locale.US);

	private static final MonetaryAmountFormat MONETARY_AMOUNT_FORMAT = MonetaryFormats.getAmountFormat(
		AmountFormatQueryBuilder.of(Locale.getDefault())
			.set(CurrencyStyle.SYMBOL)
			.build()
	);

	private final static BigDecimal           RETAIL_MULTIPLICANT    = scale(new BigDecimal(1.11));


	private static BigDecimal scale(final BigDecimal number)
	{
		return number.setScale(2, RoundingMode.HALF_UP);
	}

	public static CurrencyUnit currencyUnit()
	{
		return CURRENCY_UNIT;
	}

	public static MonetaryAmountFormat monetaryAmountFormat()
	{
		return MONETARY_AMOUNT_FORMAT;
	}

	public static MonetaryAmount money(final double number)
	{
		return money(new BigDecimal(number));
	}

	public static MonetaryAmount money(final BigDecimal number)
	{
		return RoundedMoney.of(scale(number), currencyUnit());
	}

	public static MonetaryAmount retailPrice(
		final MonetaryAmount purchasePrice
	)
	{
		return money(RETAIL_MULTIPLICANT.multiply(new BigDecimal(purchasePrice.getNumber().doubleValue())));
	}
	
	
	
	
	private final static EmbeddedStorageManager storageManager = createStorageManager();
	
	private static EmbeddedStorageManager createStorageManager()
	{
		final Configuration configuration = Configuration.Default();
		configuration.setBaseDirectory(Paths.get("data", "storage").toString());
		configuration.setChannelCount(2);

		final EmbeddedStorageFoundation<?> foundation = configuration.createEmbeddedStorageFoundation();
		foundation.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
		final EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager().start();

		if(storageManager.root() == null)
		{
			final Data data = new Data();
			storageManager.setRoot(data);
			storageManager.storeRoot();
			
			new RandomDataGenerator(
				data,
				RandomDataAmount.Small(),
				storageManager
			)
			.generate();
		}
		
		return storageManager;
	}
	
	public static EmbeddedStorageManager storageManager()
	{
		return storageManager;
	}

	public static Data data()
	{
		return (Data)storageManager.root();
	}

	public static void shutdown()
	{
		storageManager.shutdown();
	}
		
}
