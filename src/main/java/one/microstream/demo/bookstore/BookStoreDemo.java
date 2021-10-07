
package one.microstream.demo.bookstore;

import one.microstream.demo.bookstore.data.Data;
import one.microstream.persistence.binary.jdk8.types.BinaryHandlersJDK8;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;


public final class BookStoreDemo
{
	private final static EmbeddedStorageManager storageManager = createStorageManager();
	
	private static EmbeddedStorageManager createStorageManager()
	{
		final EmbeddedStorageFoundation<?> foundation = EmbeddedStorageConfiguration.Builder()
			.setStorageDirectory("data/storage")
			.setChannelCount(2)
			.createEmbeddedStorageFoundation();
		foundation.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
		final EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager().start();

		return storageManager;
	}
	
	public static EmbeddedStorageManager storageManager()
	{
		return storageManager;
	}

	public static Data data()
	{
		return (Data)storageManager.root();
	}

	public static void shutdown()
	{
		storageManager.shutdown();
	}
	
}
