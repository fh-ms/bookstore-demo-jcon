package one.microstream.demo.bookstore.util;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import javax.money.MonetaryAmount;

public final class ValidationUtils
{
	public static <T extends CharSequence> T requireNonEmpty(
		final T charSequence,
		final Supplier<String> messageSupplier
	)
	{
		if(charSequence == null)
		{
			throw new NullPointerException(messageSupplier.get());
		}
		if(charSequence.length() == 0)
		{
			throw new IllegalArgumentException(messageSupplier.get());
		}
        return charSequence;
	}

	public static <T extends CharSequence> T requireNonBlank(
		final T charSequence,
		final Supplier<String> messageSupplier
	)
	{
		requireNonEmpty(charSequence, messageSupplier);
		for(int i = 0, c = charSequence.length(); i < c; i++)
		{
			if(!Character.isWhitespace(charSequence.charAt(i)))
			{
				return charSequence;
			}
		}
		throw new IllegalArgumentException(messageSupplier.get());
	}

	public static <E, T extends Collection<E>> T requireNonEmpty(
		final T collection,
		final Supplier<String> messageSupplier
	)
	{
		if(collection == null)
		{
			throw new NullPointerException(messageSupplier.get());
		}
		if(collection.isEmpty())
		{
			throw new IllegalArgumentException(messageSupplier.get());
		}
        return collection;
	}

	public static int requirePositive(
		final int value,
		final Supplier<String> messageSupplier
	)
	{
		if(value <= 0)
		{
			throw new IllegalArgumentException(messageSupplier.get());
		}
        return value;
	}

	public static int requireZeroOrPositive(
		final int value,
		final Supplier<String> messageSupplier
	)
	{
		if(value < 0)
		{
			throw new IllegalArgumentException(messageSupplier.get());
		}
        return value;
	}
	
	public static MonetaryAmount requirePositive(
		final MonetaryAmount value,
		final Supplier<String> messageSupplier
	)
	{
		if(!value.isPositive())
		{
			throw new IllegalArgumentException(messageSupplier.get());
		}
        return value;
	}
	
	public static String isbn13Pattern()
	{
		return "^(?:ISBN(?:-13)?:? )?(?=[0-9]{13}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)97[89][- ]?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9]$";
	}
	
	public static String validateIsbn13(final String isbn13)
    {
		requireNonEmpty(isbn13, () -> "ISBN cannot be empty");

		if(!Pattern.matches(isbn13Pattern(), isbn13))
		{
			throw new IllegalArgumentException("Invalid ISBN format");
		}

		final String isbn13withoutSeparators = isbn13.replace("-", "");

		int total = 0;
		for(int i = 0; i < 12; i++)
		{
			final int digit = Integer.parseInt(isbn13withoutSeparators.substring(i, i + 1));
			total += i % 2 == 0
				? digit
				: digit * 3;
		}

		int checksum = 10 - total % 10;
		if(checksum == 10)
		{
			checksum = 0;
		}

		if(checksum != Integer.parseInt(isbn13withoutSeparators.substring(12)))
		{
			throw new IllegalArgumentException("Invalid ISBN checksum");
		}

		return isbn13;
    }
	
}
