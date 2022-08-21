package com.mkohan.library.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mkohan.library.dto.AuthorDto;
import com.mkohan.library.entity.Author;
import com.mkohan.library.repository.AuthorRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test")
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthorRepository authorRepository;

    @AfterEach
    public void destroy() {
        authorRepository.deleteAll();
    }

    @Test
    public void getAllAuthors_anonymous() throws Exception {
        testGetAllUsers();
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void getAllAuthors_authenticated() throws Exception {
        testGetAllUsers();
    }

    private void testGetAllUsers() throws Exception {
        final Author expectedAuthor = new Author("Author");
        authorRepository.save(expectedAuthor);

        String response = mockMvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Author> authors = objectMapper.readValue(response, new TypeReference<>() {
        });

        assertThat(authors).singleElement()
                .matches(a -> a.getId().equals(expectedAuthor.getId()))
                .matches(a -> a.getName().equals(expectedAuthor.getName()));
    }

    @Test
    public void getAuthorById_anonymous() throws Exception {
        testGetAuthorById();
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void getAuthorById_authenticated() throws Exception {
        testGetAuthorById();
    }

    private void testGetAuthorById() throws Exception {
        final Author expectedAuthor = new Author("Author");
        authorRepository.save(expectedAuthor);

        String response = mockMvc.perform(get("/authors/{id}", expectedAuthor.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Author author = objectMapper.readValue(response, Author.class);

        assertThat(author)
                .matches(a -> a.getId().equals(expectedAuthor.getId()))
                .matches(a -> a.getName().equals(expectedAuthor.getName()));
    }

    @Test
    public void createAuthor_anonymous() throws Exception {
        final AuthorDto authorDto = new AuthorDto(1L, "Author");

        performCreateAuthor(authorDto)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void createAuthor_authenticated() throws Exception {
        final AuthorDto authorDto = new AuthorDto();
        authorDto.setName("Author");

        String response = performCreateAuthor(authorDto)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Author author = objectMapper.readValue(response, Author.class);

        assertThat(author)
                .matches(a -> a.getName().equals(authorDto.getName()));

        assertThat(authorRepository.findAll())
                .singleElement()
                .matches(a -> a.getId().equals(author.getId()))
                .matches(a -> a.getName().equals(authorDto.getName()));
    }

    private ResultActions performCreateAuthor(final AuthorDto authorDto) throws Exception {
        RequestBuilder requestBuilder = post("/authors")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorDto))
                .contentType(MediaType.APPLICATION_JSON);

        return mockMvc.perform(requestBuilder);
    }

    @Test
    public void updateAuthor_anonymous() throws Exception {
        final AuthorDto authorDto = new AuthorDto(1L, "Author");

        performUpdateAuthor(authorDto)
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void updateAuthor_authenticated_notFound() throws Exception {
        final AuthorDto authorDto = new AuthorDto(1L, "Updated author");

        performUpdateAuthor(authorDto)
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void updateAuthor_authenticated_successful() throws Exception {
        final Author expectedAuthor = new Author("Author");
        authorRepository.save(expectedAuthor);

        final AuthorDto authorDto = new AuthorDto(expectedAuthor.getId(), "Updated author");

        performUpdateAuthor(authorDto)
                .andExpect(status().isOk());

        assertThat(authorRepository.findAll())
                .singleElement()
                .matches(a -> a.getId().equals(expectedAuthor.getId()))
                .matches(a -> a.getName().equals(authorDto.getName()));
    }

    private ResultActions performUpdateAuthor(final AuthorDto authorDto) throws Exception {
        RequestBuilder requestBuilder = put("/authors/{id}", authorDto.getId())
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorDto))
                .contentType(MediaType.APPLICATION_JSON);

        return mockMvc.perform(requestBuilder);
    }

    @Test
    public void deleteAuthor_anonymous() throws Exception {
        mockMvc.perform(delete("/authors/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void deleteAuthor_authenticated_notFound() throws Exception {
        mockMvc.perform(delete("/authors/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = ADMIN_ROLE)
    public void deleteAuthor_authenticated_successful() throws Exception {
        final Author expectedAuthor = new Author("Author");
        authorRepository.save(expectedAuthor);

        mockMvc.perform(delete("/authors/{id}", expectedAuthor.getId()))
                .andExpect(status().isOk());

        assertThat(authorRepository.findAll()).isEmpty();
    }
}
