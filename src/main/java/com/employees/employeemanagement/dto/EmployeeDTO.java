package com.employees.employeemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private Integer id;
    private String name;
    private double salary;

    private String cardNumber;

    private DepartmentDTO department;

    private Integer departmentId;

    // Many-to-Many
    private List<Integer> projectIds;
}