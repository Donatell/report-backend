package com.kvm.reportbackend.dao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<com.kvm.reportbackend.entity.Module, Integer> {
}
