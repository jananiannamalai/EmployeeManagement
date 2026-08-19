package com.employees.employeemanagement.service;

import com.employees.employeemanagement.dto.EmployeeDTO;
import com.employees.employeemanagement.entity.Employee;
import com.employees.employeemanagement.exception.EmployeeNotFoundException;
import com.employees.employeemanagement.repository.DepartmentRepository;
import com.employees.employeemanagement.repository.EmployeeRepository;
import com.employees.employeemanagement.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    private final EmployeeRepository employeeRepository =
            mock(EmployeeRepository.class);

    private final DepartmentRepository departmentRepository =
            mock(DepartmentRepository.class);

    private final ProjectRepository projectRepository =
            mock(ProjectRepository.class);

    private final ModelMapper modelMapper =
            mock(ModelMapper.class);

    private final EmployeeService employeeService =
            new EmployeeService(
                    employeeRepository,
                    departmentRepository,
                    projectRepository,
                    modelMapper
            );


    @Test
    void getEmployeeById_shouldReturnEmployee_whenEmployeeExists() {

        Employee employee = new Employee();
        employee.setId(1);
        employee.setName("Janu");
        employee.setSalary(65000);

        when(employeeRepository.findById(1))
                .thenReturn(Optional.of(employee));

        EmployeeDTO result =
                employeeService.getEmployeeById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Janu", result.getName());
        assertEquals(65000, result.getSalary());

        verify(employeeRepository).findById(1);
    }


    @Test
    void getEmployeeById_shouldThrowException_whenEmployeeDoesNotExist() {

        when(employeeRepository.findById(99))
                .thenReturn(Optional.empty());

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(99)
        );

        verify(employeeRepository).findById(99);
    }


    @Test
    void saveEmployee_shouldSaveEmployee() {

        Employee employee = new Employee();

        employee.setName("Rahul");
        employee.setSalary(70000);

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        Employee result =
                employeeService.saveEmployee(employee);

        assertNotNull(result);
        assertEquals("Rahul", result.getName());
        assertEquals(70000, result.getSalary());

        verify(employeeRepository).save(employee);
    }


    @Test
    void getAllEmployees_shouldReturnEmployees() {

        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setName("Janu");
        employee1.setSalary(65000);

        Employee employee2 = new Employee();
        employee2.setId(2);
        employee2.setName("Rahul");
        employee2.setSalary(70000);

        when(employeeRepository.findAll())
                .thenReturn(List.of(employee1, employee2));

        List<EmployeeDTO> result =
                employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("Janu", result.get(0).getName());
        assertEquals("Rahul", result.get(1).getName());

        verify(employeeRepository).findAll();
    }


    @Test
    void deleteEmployee_shouldDeleteEmployee_whenEmployeeExists() {

        when(employeeRepository.existsById(1))
                .thenReturn(true);

        employeeService.deleteEmployee(1);

        verify(employeeRepository).existsById(1);
        verify(employeeRepository).deleteById(1);
    }


    @Test
    void deleteEmployee_shouldThrowException_whenEmployeeDoesNotExist() {

        when(employeeRepository.existsById(99))
                .thenReturn(false);

        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(99)
        );

        verify(employeeRepository).existsById(99);
        verify(employeeRepository, never()).deleteById(99);
    }


    @Test
    void convertToDTO_shouldConvertEmployee() {

        Employee employee = new Employee();

        employee.setId(1);
        employee.setName("Kiran");
        employee.setSalary(80000);

        EmployeeDTO result =
                employeeService.convertToDTO(employee);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Kiran", result.getName());
        assertEquals(80000, result.getSalary());
    }


    @Test
    void convertToEntity_shouldConvertEmployee() {

        EmployeeDTO dto = new EmployeeDTO();

        dto.setName("Janani");
        dto.setSalary(90000);

        Employee result =
                employeeService.convertToEntity(dto);

        assertNotNull(result);
        assertEquals("Janani", result.getName());
        assertEquals(90000, result.getSalary());
    }


    @Test
    void updateSalary_shouldReturnUpdatedRows() {

        when(employeeRepository.updateSalary(1, 75000))
                .thenReturn(1);

        int result =
                employeeService.updateSalary(1, 75000);

        assertEquals(1, result);

        verify(employeeRepository)
                .updateSalary(1, 75000);
    }


    @Test
    void updateSalary_shouldReturnZero_whenEmployeeDoesNotExist() {

        when(employeeRepository.updateSalary(99, 75000))
                .thenReturn(0);

        int result =
                employeeService.updateSalary(99, 75000);

        assertEquals(0, result);

        verify(employeeRepository)
                .updateSalary(99, 75000);
    }


    @Test
    void updateEmployee_shouldUpdateEmployee_whenEmployeeExists() {

        Employee employee = new Employee();

        employee.setId(1);
        employee.setName("Old Name");
        employee.setSalary(50000);

        EmployeeDTO request = new EmployeeDTO();

        request.setName("Updated Name");
        request.setSalary(75000);

        when(employeeRepository.findById(1))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.save(employee))
                .thenReturn(employee);

        EmployeeDTO result =
                employeeService.updateEmployee(1, request);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals(75000, result.getSalary());

        verify(employeeRepository)
                .findById(1);

        verify(employeeRepository)
                .save(employee);
    }
}