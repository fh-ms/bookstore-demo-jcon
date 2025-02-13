package one.microstream.demo.bookstore.data;

import one.microstream.gigamap.Indexer;
import one.microstream.gigamap.IndexerString;

public interface HasAddress
{
	public static final IndexerString<HasAddress> address1Index = new IndexerString.Abstract<>()
	{
		@Override
		public String getString(final HasAddress entity)
		{
			return entity.address().address();
		}
	};

	public static final IndexerString<HasAddress> address2Index = new IndexerString.Abstract<>()
	{
		@Override
		public String getString(final HasAddress entity)
		{
			return entity.address().address2();
		}
	};

	public static final IndexerString<HasAddress> zipcodeIndex = new IndexerString.Abstract<>()
	{
		@Override
		public String getString(final HasAddress entity)
		{
			return entity.address().zipCode();
		}
	};
	
	public static final Indexer.Abstract<HasAddress, City> cityIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<City> keyType()
		{
			return City.class;
		}
		
		@Override
		public City indexEntity(final HasAddress entity)
		{
			return entity.address().city();
		}
	};
	
	public static final Indexer.Abstract<HasAddress, State> stateIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<State> keyType()
		{
			return State.class;
		}
		
		@Override
		public State indexEntity(final HasAddress entity)
		{
			return entity.address().city().state();
		}
	};
	
	public static final Indexer.Abstract<HasAddress, Country> countryIndex = new Indexer.Abstract<>()
	{
		@Override
		public Class<Country> keyType()
		{
			return Country.class;
		}
		
		@Override
		public Country indexEntity(final HasAddress entity)
		{
			return entity.address().city().state().country();
		}
	};
	
	
	public Address address();
}
