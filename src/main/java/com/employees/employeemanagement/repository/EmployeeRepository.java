package com.employees.employeemanagement.repository;

import com.employees.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    @Modifying
    @Query("""
       UPDATE Employee e
       SET e.salary = :salary
       WHERE e.id = :id
       """)
    int updateSalary(
            @Param("id") Integer id,
            @Param("salary") double salary
    );
}