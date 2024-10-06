package com.example.eowa.repository;

import com.example.eowa.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    @Query(nativeQuery = true,value = "select COUNT(*) from eowauser where email = :email")
    public int usersWithThisEmail(@Param("email") String email);
}
