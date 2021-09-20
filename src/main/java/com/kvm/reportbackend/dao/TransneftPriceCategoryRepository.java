package com.kvm.reportbackend.dao;

import com.kvm.reportbackend.entity.TransneftPriceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransneftPriceCategoryRepository extends JpaRepository<TransneftPriceCategory, Integer> {
}
