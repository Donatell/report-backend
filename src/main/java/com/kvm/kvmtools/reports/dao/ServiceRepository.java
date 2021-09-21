package com.kvm.kvmtools.reports.dao;

import com.kvm.kvmtools.reports.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
}
