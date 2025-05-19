package com.rookies3.myspringbootlab.repository;

import com.rookies3.myspringbootlab.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    // list '% param %' 효과
    List<Book> findByAuthorContainingIgnoreCase(String author);

    Optional<Book> findByTitle(String title);
    List<Book> findByTitleContainingIgnoreCase(String title);

    //@Query("SELECT b FROM Book b JOIN FETCH b.bookDetail WHERE b.id = :id")
//    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.bookDetail WHERE b.id = :id")
//    Optional<Book> findByIdWithBookDetail(@Param("id") Long bookId);
//
//    @Query("SELECT b FROM Book b JOIN FETCH b.bookDetail WHERE b.isbn = :isbn")
//    Optional<Book> findByIsbnWithBookDetail(String isbn);

    // left join으로 변경
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.bookDetail WHERE b.id = :id")
    Optional<Book> findByIdWithBookDetail(@Param("id") Long id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.bookDetail WHERE b.isbn = :isbn")
    Optional<Book> findByIsbnWithBookDetail(@Param("isbn") String isbn);

    boolean existsByIsbn(String isbn);
}
