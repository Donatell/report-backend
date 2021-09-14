package com.kvm.reportbackend.dao;

import com.kvm.reportbackend.entity.Factor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactorRepository extends JpaRepository<Factor, Integer> {
}
