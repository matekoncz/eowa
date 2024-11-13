package com.example.eowa.repository;

import com.example.eowa.model.Hour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HourRepository extends JpaRepository<Hour,Long> {
}
