package com.batch.springbatch.batch;

import com.batch.springbatch.model.Employee;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class DbtofileProcessor implements ItemProcessor<Employee,Employee> {

    @Override
    public Employee process(Employee employee) throws Exception {
        return employee;
    }
}
