
package one.microstream.demo.bookstore.data;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.QueryBuilder;

import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.data.Index.DocumentPopulator;
import one.microstream.demo.bookstore.data.Index.EntityMatcher;
import one.microstream.persistence.types.Persister;

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
	/*
	 * Transient means it is not persisted by MicroStream, but created on demand.
	 */
	private transient volatile Index<Book>   index;
	
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
		this.ensureIndex().add(book);
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
		this.ensureIndex().addAll(books);
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
	
	public <T> T compute(
		final Function<Stream<Book>, T> streamFunction
	)
	{
		return streamFunction.apply(this.isbn13ToBook.values().stream());
	}

	public <T> T computeByAuthor(
		final Author author,
		final Function<Stream<Book>, T> streamFunction
	)
	{
		final List<Book> list = this.authorToBooks.get(author);
		return streamFunction.apply(
			list != null
				? list.stream()
				: Stream.empty()
		);
	}

	public  <T> T computeByGenre(
		final Genre genre,
		final Function<Stream<Book>, T> streamFunction
	)
	{
		final List<Book> list = this.genreToBooks.get(genre);
		return streamFunction.apply(
			list != null
				? list.stream()
				: Stream.empty()
		);
	}

	public <T> T computeByPublisher(
		final Publisher publisher,
		final Function<Stream<Book>, T> streamFunction
	)
	{
		final List<Book> list = this.publisherToBooks.get(publisher);
		return streamFunction.apply(
			list != null
				? list.stream()
				: Stream.empty()
		);
	}

	public <T> T computeByLanguage(
		final Language language,
		final Function<Stream<Book>, T> streamFunction
	)
	{
		final List<Book> list = this.languageToBooks.get(language);
		return streamFunction.apply(
			list != null
				? list.stream()
				: Stream.empty()
		);
	}
	
	public <T> T computeGenres(
		final Function<Stream<Genre>, T> streamFunction
	)
	{
		return streamFunction.apply(this.genreToBooks.keySet().stream());
	}

	public <T> T computeAuthors(
		final Function<Stream<Author>, T> streamFunction
	)
	{
		return streamFunction.apply(this.authorToBooks.keySet().stream());
	}

	public <T> T computePublishers(
		final Function<Stream<Publisher>, T> streamFunction
	)
	{
		return streamFunction.apply(this.publisherToBooks.keySet().stream());
	}

	public <T> T computeLanguages(
		final Function<Stream<Language>, T> streamFunction
	)
	{
		return streamFunction.apply(this.languageToBooks.keySet().stream());
	}

	public List<Book> searchByTitle(
		final String queryText
	)
	{
		final Index<Book>  index        = this.ensureIndex();
		final QueryBuilder queryBuilder = index.createQueryBuilder();
		final Query        query        = queryBuilder.createPhraseQuery("title", queryText);
		return index.search(query, Integer.MAX_VALUE);
	}

	private Index<Book> ensureIndex()
	{
		if(this.index == null)
		{
			this.index = this.createIndex();
		}
		return this.index;
	}

	private Index<Book> createIndex()
	{
		final DocumentPopulator<Book> documentPopulator = (document, book) -> {
			document.add(new StringField("isbn13", book.isbn13(), Store.YES));
			document.add(new TextField("title", book.title(), Store.YES));
			document.add(new TextField("author", book.author().name(), Store.YES));
			document.add(new TextField("genre", book.genre().name(), Store.YES));
			document.add(new TextField("publisher", book.publisher().name(), Store.YES));
		};

		final EntityMatcher<Book> entityMatcher = document ->
			this.isbn13ToBook.get(document.get("isbn13"))
		;

		final Index<Book> index = new Index<>(
			Book.class,
			documentPopulator,
			entityMatcher
		);

		if(index.size() == 0 && this.bookCount() > 0)
		{
			index.addAll(this.isbn13ToBook.values());
		}

		return index;
	}
	
}
