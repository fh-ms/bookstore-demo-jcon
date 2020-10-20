package one.microstream.demo.bookstore.data;

import static java.util.stream.Collectors.toList;
import static one.microstream.X.coalesce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Inventory
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
		return coalesce(
			this.inventoryMap.get(book),
			0
		);
	}

	public int slotCount()
	{
		return this.inventoryMap.size();
	}

	public List<Entry<Book, Integer>> slots()
	{
		return new ArrayList<>(this.inventoryMap.entrySet());
	}

	public List<Book> books()
	{
		return this.inventoryMap.keySet().stream().collect(toList());
	}

}
