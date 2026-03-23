package com.axenovo.infotech.repository;

import com.axenovo.infotech.entity.ServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findAllByOrderBySortOrderAscIdAsc();
}
