package com.axenovo.infotech.service;

import com.axenovo.infotech.entity.ServiceItem;
import com.axenovo.infotech.repository.ServiceItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceItemService {

    private final ServiceItemRepository serviceItemRepository;

    public ServiceItemService(ServiceItemRepository serviceItemRepository) {
        this.serviceItemRepository = serviceItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ServiceItem> findAll() {
        return serviceItemRepository.findAllByOrderBySortOrderAscIdAsc();
    }

    @Transactional(readOnly = true)
    public Optional<ServiceItem> findById(Long id) {
        return serviceItemRepository.findById(id);
    }

    @Transactional
    public ServiceItem save(ServiceItem item) {
        return serviceItemRepository.save(item);
    }

    @Transactional
    public void deleteById(Long id) {
        serviceItemRepository.deleteById(id);
    }
}
