package com.rookies3.myspringbootlab.service;

import com.rookies3.myspringbootlab.controller.dto.PublisherDTO;
import com.rookies3.myspringbootlab.entity.Publisher;
import com.rookies3.myspringbootlab.exception.BusinessException;
import com.rookies3.myspringbootlab.exception.ErrorCode;
import com.rookies3.myspringbootlab.repository.BookRepository;
import com.rookies3.myspringbootlab.repository.PublisherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
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

    // 이름으로 특정 출판사 조회
    public PublisherDTO.SimpleResponse getPublisherByName(String name){
        Publisher existPublisher = publisherRepository.findByName(name)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Publisher", "name", name));
        return PublisherDTO.SimpleResponse.fromEntity(existPublisher);
    }

    // 새로운 출판사 생성, 이름 중복 검증

    // 기존 출판사 정보 수정, 이름 중복(자신 제외)을 검증

    // 출판사 삭제, 해당 출판사에 도서 있는 경우 삭제 거부
}
