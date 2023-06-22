
package one.microstream.demo.bookstore;

import one.microstream.demo.bookstore.data.Data;
import one.microstream.enterprise.cluster.nodelibrary.common.ClusterStorageManager;
import one.microstream.enterprise.cluster.nodelibrary.common.spi.ClusterStorageManagerProvider;

public class BookStoreStorageManagerProvider implements ClusterStorageManagerProvider
{
	@Override
	public ClusterStorageManager<Data> provideClusterStorageManager()
	{
		return BookStoreDemo.storageManager();
	}
}
