package one.microstream.demo.bookstore.data;

import one.microstream.gigamap.IndexerString;

public interface Named extends Comparable<Named>
{
	public static final IndexerString<Named> nameIndex = new IndexerString.Abstract<>()
	{
		@Override
		public String getString(final Named entity)
		{
			return entity.name();
		}
	};
	
	
	public String name();
	
	@Override
	public default int compareTo(final Named other)
	{
		return this.name().compareTo(other.name());
	}
}
