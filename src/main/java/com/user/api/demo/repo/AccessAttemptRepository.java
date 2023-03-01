package com.user.api.demo.repo;

import com.user.api.demo.model.AccessAttemptsReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.api.demo.model.AccessAttempt;

import java.util.List;


@Repository
public interface AccessAttemptRepository extends JpaRepository<AccessAttempt, Integer>{
    @Query("SELECT new com.user.api.demo.model.AccessAttemptsReport(aa.id, aa.timestamp, aa.accessResult) " +
            "FROM AccessAttempt aa " +
            "JOIN aa.user u " +
            "WHERE u.user_id = :userId")
    List<AccessAttemptsReport> findByUserId(@Param("userId") int userId);
}
