package one.microstream.demo.bookstore.data;

import one.microstream.gigamap.Indexer;

public interface Named extends Comparable<Named>
{
	public static final Indexer.AbstractString<Named> nameIndex = new Indexer.AbstractString<>()
	{
		@Override
		public String indexEntity(final Named entity)
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
