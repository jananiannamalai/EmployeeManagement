package com.employees.employeemanagement.service;

import com.employees.employeemanagement.entity.Department;
import com.employees.employeemanagement.exception.DepartmentNotFoundException;
import com.employees.employeemanagement.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // CREATE
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    // GET ALL
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // GET BY ID
    public Department getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new DepartmentNotFoundException(
                                "Department not found with id: " + id
                        ));
    }

    // UPDATE
    public Department updateDepartment(Integer id, Department department) {

        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() ->
                        new DepartmentNotFoundException(
                                "Department not found with id: " + id
                        ));

        existingDepartment.setName(department.getName());

        return departmentRepository.save(existingDepartment);
    }

    // DELETE
    public void deleteDepartment(Integer id) {

        if (!departmentRepository.existsById(id)) {
            throw new DepartmentNotFoundException(
                    "Department not found with id: " + id
            );
        }

        departmentRepository.deleteById(id);
    }
}