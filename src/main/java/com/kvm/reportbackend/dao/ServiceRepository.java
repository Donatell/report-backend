package com.kvm.reportbackend.dao;

import com.kvm.reportbackend.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
}
