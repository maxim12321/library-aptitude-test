package com.mkohan.library.repository;

import com.mkohan.library.entity.Author;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {

    @Override
    List<Author> findAll();

    @Override
    Optional<Author> findById(Long id);
}
