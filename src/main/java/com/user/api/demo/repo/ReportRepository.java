package com.user.api.demo.repo;

import com.user.api.demo.model.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportRepository extends JpaRepository<Action, Integer> {

}