
package one.microstream.demo.bookstore.data;

import static java.util.Objects.requireNonNull;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.QueryBuilder;

import one.microstream.exceptions.IORuntimeException;


public class Index<T> implements Closeable
{
	public static interface DocumentPopulator<T> extends BiConsumer<Document, T>
	{
	}
	
	public static interface EntityMatcher<T> extends Function<Document, T>
	{
	}

	
	private final Class<T>             entityType;
	private final DocumentPopulator<T> documentPopulator;
	private final EntityMatcher<T>     entityMatcher;
	private MMapDirectory              directory;
	private IndexWriter                writer;
	private DirectoryReader            reader;
	private IndexSearcher              searcher;

	public Index(
		final Class<T> entityType,
		final DocumentPopulator<T> documentPopulator,
		final EntityMatcher<T> entityMatcher
	)
	{
		super();
		this.entityType        = requireNonNull(entityType, () -> "EntityType cannot be null");
		this.documentPopulator = requireNonNull(documentPopulator, () -> "DocumentPopulator cannot be null");
		this.entityMatcher     = requireNonNull(entityMatcher, () -> "EntityMatcher cannot be null");
	}

	public synchronized void add(
		final T entity
	)
	{
		this.lazyInit();

		try
		{
			final Document document = new Document();
			this.documentPopulator.accept(document, entity);
			this.writer.addDocument(document);
			this.writer.flush();
			this.writer.commit();
		}
		catch(final IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public synchronized void addAll(
		final Collection<? extends T> entities
	)
	{
		this.lazyInit();

		try
		{
			for(final T entity : entities)
			{
				final Document document = new Document();
				this.documentPopulator.accept(document, entity);
				this.writer.addDocument(document);
			}

			this.writer.flush();
			this.writer.commit();
		}
		catch(final IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public synchronized void remove(
		final Query query
	)
	{
		this.lazyInit();

		try
		{
			this.writer.deleteDocuments(query);
			this.writer.flush();
			this.writer.commit();
		}
		catch(final IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public synchronized void clear()
	{
		this.lazyInit();

		try
		{
			this.writer.deleteAll();
			this.writer.flush();
			this.writer.commit();
		}
		catch(final IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public synchronized List<T> search(
		final Query query,
		final int maxResults
	)
	{
		this.lazyInit();

		try
		{
			final TopDocs topDocs = this.searcher.search(query, maxResults);
			final List<T> result = new ArrayList<>(topDocs.scoreDocs.length);
			for(final ScoreDoc scoreDoc : topDocs.scoreDocs)
			{
				final Document document = this.searcher.doc(scoreDoc.doc);
				final T entity = this.entityMatcher.apply(document);
				if(entity != null)
				{
					result.add(entity);
				}
			}
			return result;
		}
		catch(final IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	public synchronized QueryBuilder createQueryBuilder()
	{
		this.lazyInit();

		return new QueryBuilder(
			this.writer.getAnalyzer()
		);
	}

	public synchronized int size()
	{
		this.lazyInit();

		return this.searcher.getIndexReader().numDocs();
	}

	private void lazyInit()
	{
		try
		{
			if(this.directory == null)
			{
				final Path path = Paths.get(
					"data",
					"index",
					this.entityType.getSimpleName()
				);
				this.directory = new MMapDirectory(path);
				this.writer = new IndexWriter(
					this.directory,
					new IndexWriterConfig(new StandardAnalyzer())
				);
				this.searcher = new IndexSearcher(
					this.reader = DirectoryReader.open(this.writer)
				);
			}
			else
			{
				final DirectoryReader newReader = DirectoryReader.openIfChanged(this.reader);
				if(newReader != null && newReader != this.reader)
				{
					this.reader.close();
					this.reader   = newReader;
					this.searcher = new IndexSearcher(this.reader);
				}
			}
		}
		catch(final IOException e)
		{
			throw new IORuntimeException(e);
		}
	}

	@Override
	public synchronized void close() throws IOException
	{
		if(this.directory != null)
		{
			this.writer.close();
			this.reader.close();
			this.directory.close();

			this.directory = null;
			this.writer    = null;
			this.reader    = null;
			this.searcher  = null;
		}
	}

}
