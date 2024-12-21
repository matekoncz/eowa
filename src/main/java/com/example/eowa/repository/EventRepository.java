package com.example.eowa.repository;

import com.example.eowa.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

    @Query(nativeQuery = true,
            value = "select event.* from event where code = :code")
    Event findEventByInvitationCode(@Param("code") String code);
}
