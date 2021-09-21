package com.kvm.kvmtools.reports.dao;

import com.kvm.kvmtools.reports.entity.Factor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactorRepository extends JpaRepository<Factor, Integer> {
}
