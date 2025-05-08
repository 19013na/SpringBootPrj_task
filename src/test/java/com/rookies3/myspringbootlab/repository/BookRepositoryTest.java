package com.rookies3.myspringbootlab.repository;

import com.rookies3.myspringbootlab.entity.Book;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    //도서 등록 테스트
    @Test @Disabled
    @Rollback(value = false)
    void testCreateBook(){
            Book book1 = new Book();
            book1.setTitle("스프링 부트 입문");
            book1.setAuthor("홍길동");
            book1.setIsbn("9788956746425");
            book1.setPrice(30000);
            book1.setPublishDate(LocalDate.parse("2025-05-07"));

            Book addBook = bookRepository.save(book1);

            Book book2 = new Book();
            book2.setTitle("JPA 프로그래밍");
            book2.setAuthor("박둘리");
            book2.setIsbn("9788956746432");
            book2.setPrice(35000);
            book2.setPublishDate(LocalDate.parse("2025-04-30"));
            bookRepository.save(book2);

            assertThat(addBook).isNotNull();
            assertThat(addBook.getAuthor()).isEqualTo("홍길동");
            assertThat(bookRepository.findByAuthor("박둘리")).isNotNull();
    }

    //ISBN으로 도서 조회 테스트
    @Test
    void testFindByIsbn(){
        Book book = bookRepository.findByIsbn("9788956746432")
                .orElseThrow(() -> new RuntimeException("Book Not Found"));
        assertThat(book).isNotNull();
    }

    //저자명으로 도서 목록 조회 테스트
    @Test
    void testByAuthor(){
        List<Book> bookList = bookRepository.findByAuthor("홍길동");
    }

    //도서 정보 수정 테스트
    @Test
    void testUpdateBook(){
        Book book = bookRepository.findById(3L)
                .orElseThrow(() -> new RuntimeException("Book Not Found"));
        book.setPrice(25000);
        assertThat(book.getPrice()).isEqualTo(25000);
    }

    //도서 삭제 테스트
    @Test
    void testDeleteBook(){
        Book book = bookRepository.findById(4L)
                .orElseThrow(() -> new RuntimeException("Book Not Found"));
        bookRepository.delete(book);
        assertThat(bookRepository.findById(4L)).isEmpty();
    }
}