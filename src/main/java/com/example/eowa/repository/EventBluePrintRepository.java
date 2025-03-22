package com.example.eowa.repository;

import com.example.eowa.model.EventBlueprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EventBluePrintRepository extends JpaRepository<EventBlueprint,Long> {

    @Query(nativeQuery = true,
            value = "select event_blueprint.* from event_blueprint where name = :name")
    EventBlueprint findEventBlueprintByName(@Param("name") String name);

    @Query(nativeQuery = true,
            value = "select event_blueprint.* from event_blueprint where insert_user_username = :username")
    Set<EventBlueprint> getBlueprintsForUser(@Param("username") String username);
}
