package com.employees.employeemanagement.controller;

import com.employees.employeemanagement.dto.EmployeeDTO;
import com.employees.employeemanagement.entity.Employee;
import com.employees.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // TEST API
    @GetMapping
    public String test() {
        return "Employee Controller Working!";
    }


    // CREATE - 201 CREATED

    @PostMapping
    public ResponseEntity<EmployeeDTO> addEmployee(
            @Valid @RequestBody EmployeeDTO employeeDTO) {

        Employee employee =
                employeeService.convertToEntity(employeeDTO);

        Employee savedEmployee =
                employeeService.saveEmployee(employee);

        EmployeeDTO response =
                employeeService.convertToDTO(savedEmployee);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // GET ALL - 200 OK

    @GetMapping("/all")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {

        List<EmployeeDTO> employees =
                employeeService.getAllEmployees();

        return ResponseEntity.ok(employees);
    }

    // GET BY ID - 200 OK / 404

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(
            @PathVariable int id) {

        EmployeeDTO employee =
                employeeService.getEmployeeById(id);

        return ResponseEntity.ok(employee);
    }

    // UPDATE - 200 OK

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable int id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {

        EmployeeDTO updatedEmployee =
                employeeService.updateEmployee(id, employeeDTO);

        return ResponseEntity.ok(updatedEmployee);
    }


    // DELETE - 204 NO CONTENT

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @PathVariable int id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.noContent().build();
    }


    // UPDATE SALARY - 200 OK

    @PutMapping("/salary/{id}")
    public ResponseEntity<String> updateSalary(
            @PathVariable Integer id,
            @RequestParam double salary) {

        int updatedRows =
                employeeService.updateSalary(id, salary);

        if (updatedRows == 0) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Employee not found");
        }

        return ResponseEntity.ok(
                "Salary updated successfully"
        );
    }
}