
package one.microstream.demo.bookstore;

import org.eclipse.serializer.persistence.binary.jdk8.types.BinaryHandlersJDK8;
import org.eclipse.store.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;

import one.microstream.demo.bookstore.data.Data;


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
