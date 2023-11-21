package one.microstream.demo.bookstore.util;

import java.util.Optional;

import org.eclipse.serializer.reference.Lazy;

public final class LazyUtils
{
	public static <T> Optional<T> clearIfStored(final Lazy<T> lazy)
	{
		return lazy != null && lazy.isStored()
			? Optional.ofNullable(lazy.clear())
			: Optional.empty()
		;
	}
}
