package one.microstream.demo.bookstore.data;

import static java.util.stream.Collectors.toList;
import static org.eclipse.serializer.util.X.coalesce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import one.microstream.demo.bookstore.util.concurrent.ReadWriteLocked;

public class Inventory extends ReadWriteLocked
{
	private final Map<Book, Integer> inventoryMap;

	public Inventory()
	{
		this(new HashMap<>());
	}

	public Inventory(
		final Map<Book, Integer> inventoryMap
	)
	{
		super();
		this.inventoryMap = inventoryMap;
	}

	public int amount(final Book book)
	{
		return this.read(() -> coalesce(
			this.inventoryMap.get(book),
			0
		));
	}

	public int slotCount()
	{
		return this.read(() ->
			this.inventoryMap.size()
		);
	}

	public List<Entry<Book, Integer>> slots()
	{
		return this.read(() ->
			new ArrayList<>(this.inventoryMap.entrySet())
		);
	}

	public List<Book> books()
	{
		return this.read(() ->
			this.inventoryMap.keySet().stream().collect(toList())
		);
	}
	
	public <T> T compute(
		final Function<Stream<Entry<Book, Integer>>, T> streamFunction
	)
	{
		return this.read(() ->
			streamFunction.apply(
				this.inventoryMap.entrySet().stream()
			)
		);
	}

}
