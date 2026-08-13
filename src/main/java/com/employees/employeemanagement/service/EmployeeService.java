package com.employees.employeemanagement.service;

import com.employees.employeemanagement.dto.EmployeeDTO;
import com.employees.employeemanagement.entity.Department;
import com.employees.employeemanagement.entity.Employee;
import com.employees.employeemanagement.entity.IDCard;
import com.employees.employeemanagement.entity.Project;
import com.employees.employeemanagement.exception.EmployeeNotFoundException;
import com.employees.employeemanagement.repository.DepartmentRepository;
import com.employees.employeemanagement.repository.EmployeeRepository;
import com.employees.employeemanagement.repository.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           ProjectRepository projectRepository,
                           ModelMapper modelMapper) {

        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
        this.modelMapper = modelMapper;
    }

    // CREATE
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // GET ALL
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // GET BY ID
    public EmployeeDTO getEmployeeById(int id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with id: " + id));

        return convertToDTO(employee);
    }

    // UPDATE
    public EmployeeDTO updateEmployee(int id, EmployeeDTO employeeDTO) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EmployeeNotFoundException("Employee not found with id: " + id));

        employee.setName(employeeDTO.getName());
        employee.setSalary(employeeDTO.getSalary());

        // Update Department
        if (employeeDTO.getDepartmentId() != null) {

            Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() ->
                            new RuntimeException("Department not found"));

            employee.setDepartment(department);
        }

        // Update Projects
        if (employeeDTO.getProjectIds() != null && !employeeDTO.getProjectIds().isEmpty()) {

            List<Project> projects =
                    projectRepository.findAllById(employeeDTO.getProjectIds());

            employee.setProjects(projects);
        }

        Employee updatedEmployee = employeeRepository.save(employee);

        return convertToDTO(updatedEmployee);
    }

    // DELETE
    public void deleteEmployee(Integer id) {

        if (!employeeRepository.existsById(id)) {

            throw new EmployeeNotFoundException(
                    "Employee not found with id: " + id
            );
        }

        employeeRepository.deleteById(id);
    }

    // Entity → DTO


    public EmployeeDTO convertToDTO(Employee employee) {

        EmployeeDTO dto = new EmployeeDTO();

        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setSalary(employee.getSalary());

        // ID Card
        if (employee.getIdCard() != null) {
            dto.setCardNumber(employee.getIdCard().getCardNumber());
        }

        // Department
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
        }

        // Projects
        if (employee.getProjects() != null && !employee.getProjects().isEmpty()) {

            dto.setProjectIds(
                    employee.getProjects()
                            .stream()
                            .map(Project::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    // ==========================
    // DTO → Entity
    // ==========================

    public Employee convertToEntity(EmployeeDTO dto) {

        Employee employee = new Employee();

        employee.setName(dto.getName());
        employee.setSalary(dto.getSalary());

        // ID Card
        if (dto.getCardNumber() != null) {

            IDCard idCard = new IDCard();
            idCard.setCardNumber(dto.getCardNumber());

            employee.setIdCard(idCard);
        }

        // Department
        if (dto.getDepartmentId() != null) {

            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() ->
                            new RuntimeException("Department not found"));

            employee.setDepartment(department);
        }

        // Projects
        if (dto.getProjectIds() != null && !dto.getProjectIds().isEmpty()) {

            List<Project> projects =
                    projectRepository.findAllById(dto.getProjectIds());

            employee.setProjects(projects);
        }

        return employee;
    }
    @Transactional
    public int updateSalary(Integer id, double salary) {

        return employeeRepository.updateSalary(id, salary);
    }
}