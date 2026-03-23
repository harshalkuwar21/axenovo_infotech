package com.axenovo.infotech.repository;

import com.axenovo.infotech.entity.PortfolioItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    List<PortfolioItem> findAllByOrderBySortOrderAscIdAsc();
}
