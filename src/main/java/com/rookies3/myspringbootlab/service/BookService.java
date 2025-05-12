package com.rookies3.myspringbootlab.service;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.entity.Book;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<BookDTO.Response> getAllBooks(){
        List<Book> bookList = bookRepository.findAll();
        return bookList.stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    public BookDTO.Response getBookById(Long id){
        Book existBook = findBookById(id);
        return BookDTO.Response.fromEntity(existBook);
    }

    public BookDTO.Response getBookByIsbn(String isbn){
        Book existBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException("Book Not Found", HttpStatus.NOT_FOUND));
        return BookDTO.Response.fromEntity(existBook);
    }

    public List<BookDTO.Response> getBooksByAuthor(String author){
        List<Book> bookList = bookRepository.findByAuthor(author);
        return bookList.stream()
                .map(BookDTO.Response::fromEntity)
                .toList();
    }

    // 생성
    @Transactional
    public BookDTO.Response createBook(BookDTO.Request request){
        // ISBN 중복 검사
        bookRepository.findByIsbn(request.getIsbn())
                .ifPresent(book -> {
                    throw new BusinessException("Book with this ISBN already exists", HttpStatus.CONFLICT);
                });

        Book book = Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .author(request.getAuthor())
                .price(request.getPrice())
                .build();

        return BookDTO.Response.fromEntity(bookRepository.save(book));
    }

    // 수정
    @Transactional
    public BookDTO.Response updateBook(Long id, BookDTO.Request request){
        Book book = findBookById(id);

        // 변경이 필요한 필드만 업데이트
        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }

        // 확장성을 위한 추가 필드 업데이트
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }

        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }

        if (request.getPublishDate() != null) {
            book.setPublishDate(request.getPublishDate());
        }

        Book updatedBook = bookRepository.save(book);
        return BookDTO.Response.fromEntity(updatedBook);
    }

    // 삭제
    @Transactional
    public void deleteBook(Long id){
        if(!bookRepository.existsById(id)){
            throw new BusinessException("Book Not Found with ID: " + id, HttpStatus.NOT_FOUND);
        }

        Book book = findBookById(id);
        bookRepository.delete(book);
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book Not Found with ID: " + id, HttpStatus.NOT_FOUND));
    }
}
