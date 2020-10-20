
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;

public class Address
{
	private final String address;
	private final String address2;
	private final String zipCode;
	private final City   city;

	public Address(
		final String address,
		final String address2,
		final String zipCode,
		final City city
	)
	{
		super();
		this.address  = requireNonNull(address , () -> "Address cannot be null" );
		this.address2 = requireNonNull(address2, () -> "Address2 cannot be null");
		this.zipCode  = requireNonNull(zipCode , () -> "ZipCode cannot be null" );
		this.city     = requireNonNull(city    , () -> "City cannot be null"    );
	}

	public String address()
	{
		return this.address;
	}

	public String address2()
	{
		return this.address2;
	}

	public String zipCode()
	{
		return this.zipCode;
	}

	public City city()
	{
		return this.city;
	}

	@Override
	public String toString()
	{
		return "Address [address=" + this.address
			+ ", address2=" + this.address2
			+ ", zipCode=" + this.zipCode
			+ ", city=" + this.city
			+ "]";
	}

}
