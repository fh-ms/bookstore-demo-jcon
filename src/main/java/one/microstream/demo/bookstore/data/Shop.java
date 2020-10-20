
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;
import static one.microstream.demo.bookstore.util.LazyUtils.clearIfStored;
import static one.microstream.demo.bookstore.util.ValidationUtils.requireNonBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import one.microstream.reference.Lazy;

public class Shop
{
	private final String          name;
	private final Address         address;
	private final List<Employee>  employees;
	private final Lazy<Inventory> inventory;

	public Shop(
		final String  name,
		final Address address
	)
	{
		super();
		this.name      = requireNonBlank(name,    () -> "Name cannot be empty"  );
		this.address   = requireNonNull (address, () -> "Address cannot be null");
		this.employees = new ArrayList<>();
		this.inventory = Lazy.Reference(new Inventory());
	}

	Shop(
		final String         name,
		final Address        address,
		final List<Employee> employees,
		final Inventory      inventory
	)
	{
		super();
		this.name      = name;
		this.address   = address;
		this.employees = new ArrayList<>(employees);
		this.inventory = Lazy.Reference(inventory);
	}

	public String name()
	{
		return this.name;
	}

	public Address address()
	{
		return this.address;
	}

	public Stream<Employee> employees()
	{
		return this.employees.stream();
	}

	public Inventory inventory()
	{
		return this.inventory.get();
	}
	
	public void clear()
	{
		clearIfStored(this.inventory);
	}

	@Override
	public String toString()
	{
		return "Shop [name=" + this.name
			+ ", address=" + this.address
			+ ", employees=" + this.employees
			+ ", inventory=" + this.inventory + "]"
		;
	}

}
