package com.example.eowa.repository;

import com.example.eowa.model.Event;
import com.example.eowa.model.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MailRepository extends JpaRepository<Mail,Long> {

    @Query(nativeQuery = true,
            value = "select mail.* from mail where reciever_username = :username and isread = false")
    Set<Mail> getUnreadMails(@Param("username") String username);

    @Query(nativeQuery = true,
            value = "select mail.* from mail where reciever_username = :username")
    Set<Mail> getAllMails(@Param("username") String username);
}
