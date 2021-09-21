package com.kvm.kvmtools.reports.dao;

import com.kvm.kvmtools.reports.entity.TransneftPriceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransneftPriceCategoryRepository extends JpaRepository<TransneftPriceCategory, Integer> {
}
