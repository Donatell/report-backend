package com.kvm.kvmtools.reports.dao;

import com.kvm.kvmtools.reports.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository extends JpaRepository<Gender, Integer> {
}
