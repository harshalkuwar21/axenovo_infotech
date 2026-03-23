package com.axenovo.infotech.repository;

import com.axenovo.infotech.entity.ContactInquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ContactInquiryRepository extends JpaRepository<ContactInquiry, Long> {
    long countByCreatedAtAfter(LocalDateTime createdAt);

    List<ContactInquiry> findTop6ByOrderByCreatedAtDesc();
}
