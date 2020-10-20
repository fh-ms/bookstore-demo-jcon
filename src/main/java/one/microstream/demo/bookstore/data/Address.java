
package one.microstream.demo.bookstore.data;

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
		this.address  = address;
		this.address2 = address2;
		this.zipCode  = zipCode;
		this.city     = city;
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
