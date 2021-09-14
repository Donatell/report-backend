package com.kvm.reportbackend.dao;

import com.kvm.reportbackend.entity.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository extends JpaRepository<Gender, Integer> {
}
