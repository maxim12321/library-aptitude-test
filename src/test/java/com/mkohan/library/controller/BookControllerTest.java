package com.mkohan.library.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkohan.library.dto.BookDto;
import com.mkohan.library.entity.Author;
import com.mkohan.library.entity.Book;
import com.mkohan.library.repository.AuthorRepository;
import com.mkohan.library.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.mkohan.library.config.UserDetailsConfiguration.ADMIN_ROLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    private Author author;

    @BeforeEach
    public void init() {
        author = new Author("Author");
        authorRepository.save(author);
    }

    @AfterEach
    public void destroy() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void getAllBooks_anonymous() throws Exception {
        testGetAllBooks();
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void getAllBooks_authenticated() throws Exception {
        testGetAllBooks();
    }

    private void testGetAllBooks() throws Exception {
        final Book expectedBook = new Book("Book", author);
        bookRepository.save(expectedBook);

        String response = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Book> books = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertThat(books).singleElement()
                .matches(b -> b.getId().equals(expectedBook.getId()))
                .matches(b -> b.getTitle().equals(expectedBook.getTitle()))
                .matches(this::isAuthorEqualToExpected);
    }

    @Test
    public void getBookById_anonymous() throws Exception {
        testGetBookById();
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void getBookById_authenticated() throws Exception {
        testGetBookById();
    }

    private void testGetBookById() throws Exception {
        final Book expectedBook = new Book("Book", author);
        bookRepository.save(expectedBook);

        String response = mockMvc.perform(get("/books/{id}", expectedBook.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Book book = objectMapper.readValue(response, Book.class);

        assertThat(book)
                .matches(b -> b.getId().equals(expectedBook.getId()))
                .matches(b -> b.getTitle().equals(expectedBook.getTitle()))
                .matches(this::isAuthorEqualToExpected);
    }

    @Test
    public void createBook_anonymous() throws Exception {
        final BookDto bookDto = new BookDto(null, "Book", 2L);

        performCreateBook(bookDto)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void createBook_authenticated_authorNotFound() throws Exception {
        final BookDto bookDto = new BookDto(null, "Book", author.getId() + 1L);

        performCreateBook(bookDto)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void createBook_authenticated() throws Exception {
        final BookDto bookDto = new BookDto(null, "Book", author.getId());

        String response = performCreateBook(bookDto)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Book book = objectMapper.readValue(response, Book.class);

        assertThat(book)
                .matches(b -> b.getTitle().equals(bookDto.getTitle()))
                .matches(this::isAuthorEqualToExpected);

        assertThat(bookRepository.findAll())
                .singleElement()
                .matches(b -> b.getId().equals(book.getId()))
                .matches(b -> b.getTitle().equals(bookDto.getTitle()))
                .matches(this::isAuthorEqualToExpected);
    }

    private ResultActions performCreateBook(final BookDto bookDto) throws Exception {
        RequestBuilder requestBuilder = post("/books")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDto))
                .contentType(MediaType.APPLICATION_JSON);

        return mockMvc.perform(requestBuilder);
    }

    @Test
    public void updateBook_anonymous() throws Exception {
        final BookDto bookDto = new BookDto(1L, "Book", 2L);

        performUpdateBook(bookDto)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void updateBook_authenticated_notFound() throws Exception {
        final BookDto bookDto = new BookDto(1L, "Book", author.getId());

        performUpdateBook(bookDto)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void updateBook_authenticated_authorNotFound() throws Exception {
        final Book expectedBook = new Book("Book", author);
        bookRepository.save(expectedBook);

        final BookDto bookDto = new BookDto(expectedBook.getId(), "Book", author.getId() + 1L);

        performUpdateBook(bookDto)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void updateBook_authenticated_successful() throws Exception {
        final Book expectedBook = new Book("Book", author);
        bookRepository.save(expectedBook);

        final BookDto bookDto = new BookDto(expectedBook.getId(), "Updated book", author.getId());

        performUpdateBook(bookDto)
                .andExpect(status().isOk());

        assertThat(bookRepository.findAll())
                .singleElement()
                .matches(a -> a.getId().equals(expectedBook.getId()))
                .matches(a -> a.getTitle().equals(bookDto.getTitle()))
                .matches(this::isAuthorEqualToExpected);
    }

    private ResultActions performUpdateBook(final BookDto bookDto) throws Exception {
        RequestBuilder requestBuilder = put("/books/{id}", bookDto.getId())
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDto))
                .contentType(MediaType.APPLICATION_JSON);

        return mockMvc.perform(requestBuilder);
    }

    @Test
    public void deleteBook_anonymous() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void deleteBook_authenticated_notFound() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void deleteBook_authenticated_successful() throws Exception {
        final Book expectedBook = new Book("Book", author);
        bookRepository.save(expectedBook);

        mockMvc.perform(delete("/books/{id}", expectedBook.getId()))
                .andExpect(status().isOk());

        assertThat(bookRepository.findAll()).isEmpty();
    }

    private boolean isAuthorEqualToExpected(Book book) {
        return book.getAuthor().getId().equals(author.getId()) &&
                book.getAuthor().getName().equals(author.getName());
    }
}
