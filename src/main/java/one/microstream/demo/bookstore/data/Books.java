
package one.microstream.demo.bookstore.data;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.serializer.persistence.types.Persister;

import one.microstream.demo.bookstore.BookStoreDemo;

public class Books
{
	/*
	 * Multiple maps holding references to the books, for a faster lookup.
	 */
	private final Map<String, Book>          isbn13ToBook     = new HashMap<>(1024);
	private final Map<Author, List<Book>>    authorToBooks    = new HashMap<>(512);
	private final Map<Genre, List<Book>>     genreToBooks     = new HashMap<>(512);
	private final Map<Publisher, List<Book>> publisherToBooks = new HashMap<>(1024);
	private final Map<Language, List<Book>>  languageToBooks  = new HashMap<>(32);
	
	public Books()
	{
		super();
	}
	
	public void add(final Book book)
	{
		this.add(book, BookStoreDemo.storageManager());
	}
	
	public void add(
		final Book book,
		final Persister persister
	)
	{
		this.addToCollections(book);
		this.storeCollections(persister);
	}
	
	public void addAll(final Collection<? extends Book> books)
	{
		this.addAll(books, BookStoreDemo.storageManager());
	}
	
	public void addAll(
		final Collection<? extends Book> books,
		final Persister persister
	)
	{
		books.forEach(this::addToCollections);
		this.storeCollections(persister);
	}
	
	private void addToCollections(
		final Book book
	)
	{
		this.isbn13ToBook.put(book.isbn13(), book);
		addToMap(this.authorToBooks, book.author(), book);
		addToMap(this.genreToBooks, book.genre(), book);
		addToMap(this.publisherToBooks, book.publisher(), book);
		addToMap(this.languageToBooks, book.language(), book);
	}

	private static <K> void addToMap(
		final Map<K, List<Book>> map,
		final K key,
		final Book book
	)
	{
		map.computeIfAbsent(
			key,
			k -> new ArrayList<>(1024)
		)
		.add(book);
	}
	
	private void storeCollections(final Persister persister)
	{
		persister.storeAll(
			this.isbn13ToBook,
			this.authorToBooks,
			this.genreToBooks,
			this.publisherToBooks,
			this.languageToBooks
		);
	}

	public List<Book> all()
	{
		return this.isbn13ToBook.values().stream()
			.sorted()
			.collect(toList())
		;
	}

	public List<Author> authors()
	{
		return this.authorToBooks.keySet().stream()
			.sorted()
			.collect(toList())
		;
	}

	public List<Genre> genres()
	{
		return this.genreToBooks.keySet().stream()
			.sorted()
			.collect(toList())
		;
	}

	public List<Publisher> publishers()
	{
		return this.publisherToBooks.keySet().stream()
			.sorted()
			.collect(toList())
		;
	}

	public List<Language> languages()
	{
		return this.languageToBooks.keySet().stream()
			.sorted()
			.collect(toList())
		;
	}

	public int bookCount()
	{
		return this.isbn13ToBook.size();
	}

	public Book ofIsbn13(
		final String isbn13
	)
	{
		return this.isbn13ToBook.get(isbn13);
	}

}
