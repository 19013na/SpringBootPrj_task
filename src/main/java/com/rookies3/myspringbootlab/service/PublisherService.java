package com.rookies3.myspringbootlab.service;

import com.rookies3.myspringbootlab.controller.dto.BookDTO;
import com.rookies3.myspringbootlab.controller.dto.PublisherDTO;
import com.rookies3.myspringbootlab.entity.Book;
import com.rookies3.myspringbootlab.entity.Publisher;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.exception.ErrorCode;
import com.rookies3.myspringbootlab.repository.BookRepository;
import com.rookies3.myspringbootlab.repository.PublisherRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PublisherService {

    private final PublisherRepository publisherRepository;
    private final BookRepository bookRepository;

    // 모든 출판사 조회, 각 출판사의 도서 수 포함
    public List<PublisherDTO.SimpleResponse> getAllPublishers(){
        List<Publisher> publishers = publisherRepository.findAll();
        return publishers.stream()
                .map(publisher -> {
                    Long publisherCount = bookRepository.countByPublisherId(publisher.getId());
                    return PublisherDTO.SimpleResponse.builder()
                            .id(publisher.getId())
                            .name(publisher.getName())
                            .establishedDate(publisher.getEstablishedDate())
                            .address(publisher.getAddress())
                            .bookCount(publisherCount)
                            .build();
                })
                .toList();
    }

    // Id로 출판사와 모든 도서 정보 조회
    public PublisherDTO.Response getPublisherById(Long id){
        Publisher existPublisher = publisherRepository.findByIdWithBooks(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Publisher", "id", id));
        return PublisherDTO.Response.fromEntity(existPublisher);
    }

    // 출판사별 도서 목록 조회 - 도서 정보 (publisher, detail) 있는 경우
    public List<BookDTO.Response> getPublisherDetailById(Long id){
        List<Book> existBook = bookRepository.findByPublisherId(id);
        Publisher existPublisher = publisherRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Publisher", "id", id));
        PublisherDTO.SimpleResponse responsePublisher = PublisherDTO.SimpleResponse.fromEntity(existPublisher);

        return existBook.stream()
                .map(book -> {
                    return BookDTO.Response.builder()
                            .id(book.getId())
                            .title(book.getTitle())
                            .author(book.getAuthor())
                            .isbn(book.getIsbn())
                            .price(book.getPrice())
                            .publishDate(book.getPublishDate())
                            .publisher(responsePublisher)
                            .detail(BookDTO.BookDetailResponse.fromEntity(book.getBookDetail()))
                            .build();
                })
                .toList();
    }



    // 이름으로 특정 출판사 조회
    public PublisherDTO.SimpleResponse getPublisherByName(String name){
        Publisher existPublisher = publisherRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Publisher", "name", name));
        return PublisherDTO.SimpleResponse.fromEntity(existPublisher);
    }

//    // 출판사별 도서 목록 조회
//    public PublisherDTO.Response getBookDetailById(Long id) {
//        Publisher existPublisher = publisherRepository.findById()
//                .orElseTr
//    }

    // 새로운 출판사 생성
    @Transactional
    public PublisherDTO.Response createPublisher(PublisherDTO.Request request){
        // 이름 중복 검증
        if(publisherRepository.existsByName(request.getName())){
            throw new BusinessException(ErrorCode.PUBLISHER_NAME_DUPLICATE,
                    request.getName());
        }
        Publisher publisher = Publisher.builder()
                .name(request.getName())
                .establishedDate(request.getEstablishedDate())
                .address(request.getAddress())
                .build();

        Publisher savedPublisher = publisherRepository.save(publisher);
        return PublisherDTO.Response.fromEntity(savedPublisher);
    }

    // 기존 출판사 정보 수정
    @Transactional
    public PublisherDTO.Response updatePublisher(Long id, PublisherDTO.Request request){
        // 이름 중복(자신 제외)을 검증
        Publisher existPublisher = publisherRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Publisher", "id", id));

        if(existPublisher.getName().equals(request.getName())){
            throw new BusinessException(ErrorCode.PUBLISHER_NAME_DUPLICATE,
                    request.getName());
        }

        existPublisher.setName(request.getName());
        existPublisher.setAddress(request.getAddress());
        existPublisher.setEstablishedDate(request.getEstablishedDate());

        Publisher updatedPublisher = publisherRepository.save(existPublisher);
        return PublisherDTO.Response.fromEntity(updatedPublisher);
    }

    // 출판사 삭제
    @Transactional
    public void deletePublisher(Long id){
        // 원래 없는 경우 & 이미 삭제된 경우
        if(!publisherRepository.existsById(id)){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Publisher", "id", id);
        }
        // 해당 출판사에 도서 있는 경우 삭제 거부
        if(bookRepository.countByPublisherId(id) > 0){
            throw new BusinessException(ErrorCode.PUBLISHER_HAS_BOOKS,
                    id, bookRepository.countByPublisherId(id));
        }
        publisherRepository.deleteById(id);
    }
}
