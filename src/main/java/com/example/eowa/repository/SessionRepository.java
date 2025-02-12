package com.example.eowa.repository;

import com.example.eowa.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session,String> {

    @Query(nativeQuery = true,value = "delete from session where jsessionid = :id")
    @Modifying
    int removeSessionById(@Param("id") String id);
}
