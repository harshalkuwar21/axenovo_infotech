package com.axenovo.infotech.service;

import com.axenovo.infotech.entity.ContactInquiry;
import com.axenovo.infotech.model.ContactInquiryForm;
import com.axenovo.infotech.repository.ContactInquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactInquiryService {

    private final ContactInquiryRepository contactInquiryRepository;

    public ContactInquiryService(ContactInquiryRepository contactInquiryRepository) {
        this.contactInquiryRepository = contactInquiryRepository;
    }

    @Transactional
    public ContactInquiry save(ContactInquiryForm form) {
        ContactInquiry inquiry = new ContactInquiry();
        inquiry.setName(form.getName());
        inquiry.setEmail(form.getEmail());
        inquiry.setPhone(form.getPhone());
        inquiry.setProjectBrief(form.getProjectBrief());
        return contactInquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return contactInquiryRepository.count();
    }

    @Transactional(readOnly = true)
    public long countSinceDays(int days) {
        return contactInquiryRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(days));
    }

    @Transactional(readOnly = true)
    public List<ContactInquiry> findRecent() {
        return contactInquiryRepository.findTop6ByOrderByCreatedAtDesc();
    }
}
