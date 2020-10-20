
package one.microstream.demo.bookstore;

import java.nio.file.Paths;

import one.microstream.demo.bookstore.data.Data;
import one.microstream.jdk8.java.util.BinaryHandlersJDK8;
import one.microstream.storage.configuration.Configuration;
import one.microstream.storage.types.EmbeddedStorageFoundation;
import one.microstream.storage.types.EmbeddedStorageManager;


public final class BookStoreDemo
{
	private final static EmbeddedStorageManager storageManager = createStorageManager();
	
	private static EmbeddedStorageManager createStorageManager()
	{
		final Configuration configuration = Configuration.Default();
		configuration.setBaseDirectory(Paths.get("data", "storage").toString());
		configuration.setChannelCount(2);

		final EmbeddedStorageFoundation<?> foundation = configuration.createEmbeddedStorageFoundation();
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
