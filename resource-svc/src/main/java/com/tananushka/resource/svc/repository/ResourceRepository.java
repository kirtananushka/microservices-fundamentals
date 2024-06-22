package com.tananushka.resource.svc.repository;

import com.tananushka.resource.svc.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    void deleteByIdIn(List<Long> ids);

    @Query("SELECT r.id FROM Resource r")
    List<Long> findAllIds();
}
