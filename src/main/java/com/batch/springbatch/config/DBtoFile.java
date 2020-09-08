package com.batch.springbatch.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.batch.springbatch.batch.DbtofileProcessor;
import com.batch.springbatch.model.Employee;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class DBtoFile {
    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource datasource;

    @Bean
    public JdbcCursorItemReader<Employee> reader() {
        JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(this.datasource);
        reader.setSql("SELECT id,name,dept,salary FROM employee");
        reader.setRowMapper(new EmployeeRowMapper());

        return reader;
    }

    public class EmployeeRowMapper implements RowMapper<Employee> {

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
    public DbtofileProcessor processor1(){
        return new DbtofileProcessor();
    }

    @Bean
    public FlatFileItemWriter<Employee> writer(){
        FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<Employee>();
        writer.setResource(new FileSystemResource("newemployee.csv"));
        writer.setLineAggregator(new DelimitedLineAggregator<Employee>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<Employee>() {{
                setNames(new String [] {"id","name","dept","salary"});
            }});
        }});
        return writer;
    }


     @Bean
     public Step step1(){
         return stepBuilderFactory.get("step1")
                .<Employee,Employee> chunk(10)
                .reader(reader())
                .processor(processor1())
                .writer(writer())
                .build();         
     }


     @Bean
     public Job exportEmployeeJob(){
         return jobBuilderFactory.get("exportEmployeeJob")
                    .incrementer(new RunIdIncrementer())
                    .start(step1())
                    .build();
     }
     
}
