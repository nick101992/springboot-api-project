package com.user.api.demo.repo;

import com.user.api.demo.model.FileEntity;
import com.user.api.demo.model.User;
import com.user.api.demo.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileEntityRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findAll();

    boolean existsById(Long id);

    @Query("SELECT f FROM FileEntity f WHERE f.username = :username")
    List<FileEntity> findByUsername(@Param("username") String username);

    Optional<FileEntity> findByName(String name);

    Optional<FileEntity> findByNameAndUsername(String name, String username) ;
}