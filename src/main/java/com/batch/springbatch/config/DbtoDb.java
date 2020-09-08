package com.batch.springbatch.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;

import javax.sql.DataSource;

import com.batch.springbatch.model.Employee;
import com.batch.springbatch.model.EmployeeBackup;
import com.batch.springbatch.repository.EmployeeBackupRepository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class DbtoDb {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource datasource;

    @Autowired
    EmployeeBackupRepository employeeBackupRepository;

    @Bean
    public JdbcCursorItemReader<Employee> databaseReader(){
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<Employee>();
        reader.setDataSource(this.datasource);
        reader.setSql("SELECT id,name,dept,salary FROM employee");
        reader.setRowMapper(new EmployeeMapper());
        return reader;
    }

    public class EmployeeMapper implements RowMapper<Employee> {

        @Override
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee employee = new Employee();
            employee.setId(rs.getInt("id"));
            employee.setName(rs.getString("name"));
            employee.setDept(rs.getString("dept"));
            employee.setSalary(rs.getInt("salary"));
            return employee;
        }
         
     }

    @Bean
    public ItemProcessor<Employee,EmployeeBackup> databaseProcessor() {
        return (employee) -> {
            EmployeeBackup empBackup = new EmployeeBackup();
            empBackup.setId(employee.getId());
            empBackup.setName(employee.getName());
            empBackup.setDept(employee.getDept());
            empBackup.setSalary(employee.getSalary());
            return empBackup;
        };
    } 

    @Bean 
    public ItemWriter<EmployeeBackup> databaseWriter(){
        return (employeebackupdata) -> {
            employeeBackupRepository.saveAll(employeebackupdata);
        };
    }

    @Bean
    public Step step2(){
        return stepBuilderFactory.get("step2")
                        .<Employee,EmployeeBackup> chunk(10)
                        .reader(databaseReader())
                        .processor(databaseProcessor())
                        .writer(databaseWriter())
                        .build();
    }

    @Bean
    public Job exportDBtoDB (){
        return jobBuilderFactory.get("exportDBtoDB")
                        .incrementer(new RunIdIncrementer())
                        .start(step2())
                        .build();
    }
    
}
