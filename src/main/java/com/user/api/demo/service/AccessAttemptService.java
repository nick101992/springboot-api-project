package com.user.api.demo.service;

import com.user.api.demo.model.AccessAttemptsReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.user.api.demo.model.AccessAttempt;
import com.user.api.demo.repo.AccessAttemptRepository;

import java.util.List;

@Service
public class AccessAttemptService {

	@Autowired
	AccessAttemptRepository repo;

    public AccessAttempt save(AccessAttempt AccessAttempt) {
        return repo.save(AccessAttempt);
    }

    public List<AccessAttemptsReport> findByUserId(int id) {
        return repo.findByUserId(id);
    }

}
