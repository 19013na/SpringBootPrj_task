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

    final private BookRepository bookRepository;

    public List<BookDTO.BookResponse> getAllBooks(){
        List<Book> bookList = bookRepository.findAll();
        return bookList.stream()
                .map(BookDTO.BookResponse::from)
                .toList();
    }

    public BookDTO.BookResponse getBookById(Long id){
        Book existBook = getBook(id);
        return BookDTO.BookResponse.from(existBook);
    }

    public BookDTO.BookResponse getBookByIsbn(String isbn){
        Book existBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BusinessException("Book Not Found", HttpStatus.NOT_FOUND));
        return BookDTO.BookResponse.from(existBook);
    }

    public List<BookDTO.BookResponse> getBooksByAuthor(String author){
        List<Book> bookList = bookRepository.findByAuthor(author);
        return bookList.stream()
                .map(BookDTO.BookResponse::from)
                .toList();
    }

    // 생성
    @Transactional
    public BookDTO.BookResponse createUser(BookDTO.BookCreateRequest request){
        Book book = request.toEntity();
        return BookDTO.BookResponse.from(bookRepository.save(book));
    }

    // 수정
    @Transactional
    public BookDTO.BookResponse updateBook(Long id, BookDTO.BookUpdateRequest request){
        Book book = getBook(id);

        book.setAuthor(request.getAuthor());
        book.setPrice(request.getPrice());
        book.setTitle(request.getTitle());
        book.setPublishDate(request.getPublishDate());
        return BookDTO.BookResponse.from(book);
    }

    // 삭제
    @Transactional
    public void deleteBook(Long id){
        Book book = getBook(id);
        bookRepository.delete(book);
    }

    public Book getBook(Long id){
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Book Not Found", HttpStatus.NOT_FOUND));
    }
}
