
package one.microstream.demo.bookstore.data;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.serializer.persistence.types.Persister;

import one.microstream.demo.bookstore.util.concurrent.ReadWriteLocked;
import one.microstream.gigamap.Condition;
import one.microstream.gigamap.GigaMap;
import one.microstream.gigamap.GigaQuery;

public class Books extends ReadWriteLocked
{
	private final GigaMap<Book> map = GigaMap.<Book>Builder()
		.withBitmapIdentityIndex(Book.isbn13Index)
		.withBitmapIndex(Named.nameIndex)
		.withBitmapIndex(Book.authorIndex)
		.withBitmapIndex(Book.genreIndex)
		.withBitmapIndex(Book.publisherIndex)
		.withBitmapIndex(Book.languageIndex)
		.build()
	;
	
	
	public Books()
	{
		super();
	}
	
	public void add(final Book book)
	{
		this.write(() ->
		{
			this.map.add(book);
			this.map.store();
		});
	}
	
	public void add(
		final Book book,
		final Persister persister
	)
	{
		this.write(() ->
		{
			this.map.add(book);
			persister.store(this.map);
		});
	}
	
	public void addAll(final Collection<? extends Book> books)
	{
		this.write(() ->
		{
			this.map.addAll(books);
			this.map.store();
		});
	}
	
	public void addAll(
		final Collection<? extends Book> books,
		final Persister persister
	)
	{
		this.write(() ->
		{
			this.map.addAll(books);
			persister.store(this.map);
		});
	}

	public List<Author> authors()
	{
		return this.read(() ->
			Book.authorIndex.resolveKeys(this.map)
		);
	}

	public List<Genre> genres()
	{
		return this.read(() ->
			Book.genreIndex.resolveKeys(this.map)
		);
	}

	public List<Publisher> publishers()
	{
		return this.read(() ->
			Book.publisherIndex.resolveKeys(this.map)
		);
	}

	public List<Language> languages()
	{
		return this.read(() ->
			Book.languageIndex.resolveKeys(this.map)
		);
	}

	public long bookCount()
	{
		return this.read(() ->
			this.map.size()
		);
	}

	public Book ofIsbn13(
		final String isbn13
	)
	{
		return this.read(() ->
			this.map.query(Book.isbn13Index.is(isbn13)).findFirst().orElse(null)
		);
	}
	
	public List<Book> searchByTitle(final String queryText)
	{
		return this.read(() ->
			this.map.query(Named.nameIndex.containsIgnoreCase(queryText)).toList()
		);
	}
	
	public List<Book> allByAuthor(final Author author)
	{
		return this.read(() ->
			this.map.query(Book.authorIndex.is(author)).toList()
		);
	}
	
	public List<Book> allByGenre(final Genre genre)
	{
		return this.read(() ->
			this.map.query(Book.genreIndex.is(genre)).toList()
		);
	}
	
	public List<Book> allByPublisher(final Publisher publisher)
	{
		return this.read(() ->
			this.map.query(Book.publisherIndex.is(publisher)).toList()
		);
	}
	
	public List<Book> allByLanguage(final Language language)
	{
		return this.read(() ->
			this.map.query(Book.languageIndex.is(language)).toList()
		);
	}
	
	public <R> R compute(
		final Condition<Book>           condition,
		final int                       offset,
		final int                       limit,
		final Function<Stream<Book>, R> function
	)
	{
		return this.read(() ->
		{
			GigaQuery<Book> query = this.map.query();
			if(condition != null)
			{
				query = query.and(condition);
			}
			return function.apply(query.toList(offset, limit).stream());
		});
	}
	
}
