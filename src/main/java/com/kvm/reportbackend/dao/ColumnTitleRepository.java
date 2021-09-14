package com.kvm.reportbackend.dao;

import com.kvm.reportbackend.entity.ColumnTitle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnTitleRepository
		extends JpaRepository<ColumnTitle, Integer> {
}
