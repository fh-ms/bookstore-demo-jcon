
package one.microstream.demo.bookstore.controller;

import java.util.List;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import one.microstream.demo.bookstore.BookStoreDemo;
import one.microstream.demo.bookstore.data.Book;


@Controller("/")
public class ExampleController
{
	@Get("/books")
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse<List<Book>> books()
	{
		return HttpResponse.ok(BookStoreDemo.data().books().all());
	}
}
