package com.rookies3.myspringbootlab.controller;

import com.rookies3.myspringbootlab.entity.Book;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    @PostMapping
    public Book create(@RequestBody Book book){
        return bookRepository.save(book);
    }

    @GetMapping
    public List<Book> getAll(){
        return bookRepository.findAll();
    }

    //ID로 특정 도서 조회
    //map() / orElse() 를 사용
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id){
        Optional<Book> book = bookRepository.findById(id);
        return book.map(ResponseEntity::ok)
                .orElse(new ResponseEntity("Book Not Found", HttpStatus.NOT_FOUND));
    }

    //ISBN으로 도서 조회
    //BusinessException 과 ErrorObject / DefaultExceptionAdvice 를 사용
    @GetMapping("/isbn/{isbn}")
    public Book getUserByIsbn(@PathVariable String isbn){
        Optional<Book> book = bookRepository.findByIsbn(isbn);
        return book.orElseThrow(() ->
                new BusinessException("Book Not Found", HttpStatus.NOT_FOUND));
    }

    //도서 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<Book> updateBookById(@PathVariable Long id, @RequestBody Book book){
        Book findBook = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book Not Found", HttpStatus.NOT_FOUND));
        findBook.setPrice(book.getPrice());
        findBook.setAuthor(book.getAuthor());
        return ResponseEntity.ok(bookRepository.save(findBook));
    }

    //도서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookById(@PathVariable Long id){
        Book findBook = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book Not Found", HttpStatus.NOT_FOUND));
        bookRepository.delete(findBook);
        return ResponseEntity.ok("Book deleted");
    }
}
