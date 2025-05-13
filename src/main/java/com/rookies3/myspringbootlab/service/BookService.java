package com.rookies3.myspringbootlab.service;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.entity.Book;
import com.rookies3.myspringbootlab.entity.BookDetail;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.exception.ErrorCode;
import com.rookies3.myspringbootlab.repository.BookDetailRepository;
import com.rookies3.myspringbootlab.repository.BookRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.ISBN;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookDetailRepository bookDetailRepository;

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
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Book", "isbn", isbn));
        return BookDTO.Response.fromEntity(existBook);
    }

    public BookDTO.Response getBookByTitle(String title) {
        Book existBook = bookRepository.findByTitle(title)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Book", "title", title));
        return BookDTO.Response.fromEntity(existBook);

    }

    public List<BookDTO.Response> getBooksByAuthor(String author){
        List<Book> bookList = bookRepository.findByAuthorContaining(author);
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
                    throw new BusinessException(ErrorCode.RESOURCE_DUPLICATE, "Book", "ISBN", request.getIsbn());
                });

        Book book = Book.builder()
                .isbn(request.getIsbn())
                .title(request.getTitle())
                .author(request.getAuthor())
                .price(request.getPrice())
                .build();

        if(request.getDetailRequest() != null){
            BookDetail bookDetail = BookDetail.builder()
                    .description(request.getDetailRequest().getDescription())
                    .language(request.getDetailRequest().getLanguage())
                    .pageCount(request.getDetailRequest().getPageCount())
                    .publisher(request.getDetailRequest().getPublisher())
                    .coverImageUrl(request.getDetailRequest().getCoverImageUrl())
                    .edition(request.getDetailRequest().getEdition())
                    .build();
            book.setBookDetail(bookDetail);
        }
        return BookDTO.Response.fromEntity(bookRepository.save(book));
    }

    // 수정
    @Transactional
    public BookDTO.Response updateBook(Long id, BookDTO.Request request){
        Book book = findBookById(id);

        // Book : 변경이 필요한 필드만 업데이트
        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }

        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }

        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }

        if (request.getPublishDate() != null) {
            book.setPublishDate(request.getPublishDate());
        }

        // BookDetail
        if (request.getDetailRequest() != null){
            BookDetail bookDetail = book.getBookDetail();

            if(bookDetail == null){
                bookDetail = new BookDetail();
                bookDetail.setBook(book);
                book.setBookDetail(bookDetail);
            }

            // BookDetail 업데이트
            if (request.getDetailRequest().getDescription() != null) {
                bookDetail.setDescription(request.getDetailRequest().getDescription());
            }
            if (request.getDetailRequest().getLanguage() != null) {
                bookDetail.setLanguage(request.getDetailRequest().getLanguage());
            }
            if (request.getDetailRequest().getPageCount() != null) {
                bookDetail.setPageCount(request.getDetailRequest().getPageCount());
            }
//            bookDetail.setPublisher(request.getDetailRequest().getPublisher());
//            bookDetail.setCoverImageUrl(request.getDetailRequest().getCoverImageUrl());
//            bookDetail.setEdition(request.getDetailRequest().getEdition());
        }

        Book updatedBook = bookRepository.save(book);
        return BookDTO.Response.fromEntity(updatedBook);
    }

    // 삭제
    @Transactional
    public void deleteBook(Long id){
        if(!bookRepository.existsById(id)){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Book", "id", id);
        }
        bookRepository.deleteById(id);
    }

    private Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Book", "id", id));
    }
}
