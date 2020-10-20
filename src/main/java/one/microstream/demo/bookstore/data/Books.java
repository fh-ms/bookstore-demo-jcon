
package one.microstream.demo.bookstore.data;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
