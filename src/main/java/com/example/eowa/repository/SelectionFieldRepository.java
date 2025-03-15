package com.example.eowa.repository;

import com.example.eowa.model.SelectionField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelectionFieldRepository extends JpaRepository<SelectionField,Long> {
}
