package com.grandcity.backend.repository;

import com.grandcity.backend.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, String> {

    List<Certificate> findAllByOrderBySortOrderAsc();
}
