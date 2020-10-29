package one.microstream.demo.bookstore.data;

public interface Named extends Comparable<Named>
{
	public String name();
	
	@Override
	public default int compareTo(final Named other)
	{
		return this.name().compareTo(other.name());
	}
}
