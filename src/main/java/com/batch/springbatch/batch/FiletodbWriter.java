package com.batch.springbatch.batch;

import java.util.List;

import com.batch.springbatch.model.Employee;
import com.batch.springbatch.repository.EmployeeRepository;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FiletodbWriter implements ItemWriter<Employee> {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Override
    public void write(List<? extends Employee> employees) throws Exception {

        System.out.println("Data Saved for Employee" + employees);
        employeeRepository.saveAll(employees);
    }
    
}
