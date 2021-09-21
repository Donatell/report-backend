package com.kvm.kvmtools.reports.dao;

import com.kvm.kvmtools.reports.entity.ColumnTitle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnTitleRepository
		extends JpaRepository<ColumnTitle, Integer> {
}
