package com.batch.springbatch.repository;

import com.batch.springbatch.model.EmployeeBackup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeBackupRepository extends JpaRepository<EmployeeBackup,Integer>{
    
}
