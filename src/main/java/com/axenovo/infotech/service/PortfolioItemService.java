package com.axenovo.infotech.service;

import com.axenovo.infotech.entity.PortfolioItem;
import com.axenovo.infotech.repository.PortfolioItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioItemService {

    private final PortfolioItemRepository portfolioItemRepository;

    public PortfolioItemService(PortfolioItemRepository portfolioItemRepository) {
        this.portfolioItemRepository = portfolioItemRepository;
    }

    @Transactional(readOnly = true)
    public List<PortfolioItem> findAll() {
        return portfolioItemRepository.findAllByOrderBySortOrderAscIdAsc();
    }

    @Transactional(readOnly = true)
    public Optional<PortfolioItem> findById(Long id) {
        return portfolioItemRepository.findById(id);
    }

    @Transactional
    public PortfolioItem save(PortfolioItem item) {
        return portfolioItemRepository.save(item);
    }

    @Transactional
    public void deleteById(Long id) {
        portfolioItemRepository.deleteById(id);
    }
}
