package com.employees.employeemanagement.service;

import com.employees.employeemanagement.dto.ProjectDTO;
import com.employees.employeemanagement.entity.Employee;
import com.employees.employeemanagement.entity.Project;
import com.employees.employeemanagement.exception.ProjectNotFoundException;
import com.employees.employeemanagement.repository.EmployeeRepository;
import com.employees.employeemanagement.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectService(ProjectRepository projectRepository,
                          EmployeeRepository employeeRepository) {

        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
    }


    // CREATE PROJECT

    public Project saveProject(Project project) {

        // Save project first so it gets an ID
        Project savedProject = projectRepository.save(project);

        // Get employees connected to this project
        if (project.getEmployees() != null) {

            for (Employee employee : project.getEmployees()) {

                if (employee.getProjects() == null) {
                    employee.setProjects(new ArrayList<>());
                }

                employee.getProjects().add(savedProject);

                employeeRepository.save(employee);
            }
        }

        return savedProject;
    }


    // GET ALL PROJECTS

    public List<ProjectDTO> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }


    // GET PROJECT BY ID

    public ProjectDTO getProjectById(Integer id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException(
                                "Project not found with id: " + id
                        ));

        return convertToDTO(project);
    }


    // UPDATE PROJECT

    public ProjectDTO updateProject(
            Integer id,
            ProjectDTO projectDTO) {

        Project existingProject =
                projectRepository.findById(id)
                        .orElseThrow(() ->
                                new ProjectNotFoundException(
                                        "Project not found with id: " + id
                                ));

        // Update project name
        existingProject.setName(projectDTO.getName());

        // Remove project from existing employees


        if (existingProject.getEmployees() != null) {

            for (Employee oldEmployee :
                    existingProject.getEmployees()) {

                if (oldEmployee.getProjects() != null) {

                    oldEmployee.getProjects()
                            .removeIf(project ->
                                    project.getId()
                                            .equals(existingProject.getId())
                            );

                    employeeRepository.save(oldEmployee);
                }
            }
        }


        // Add project to new employees


        List<Employee> newEmployees = new ArrayList<>();

        if (projectDTO.getEmployeeIds() != null &&
                !projectDTO.getEmployeeIds().isEmpty()) {

            newEmployees =
                    employeeRepository.findAllById(
                            projectDTO.getEmployeeIds()
                    );

            for (Employee employee : newEmployees) {

                if (employee.getProjects() == null) {
                    employee.setProjects(new ArrayList<>());
                }

                employee.getProjects().add(existingProject);

                employeeRepository.save(employee);
            }
        }

        // Update inverse side
        existingProject.setEmployees(newEmployees);

        // Save project
        Project updatedProject =
                projectRepository.save(existingProject);

        return convertToDTO(updatedProject);
    }


    // DELETE PROJECT

    public void deleteProject(Integer id) {

        Project project =
                projectRepository.findById(id)
                        .orElseThrow(() ->
                                new ProjectNotFoundException(
                                        "Project not found with id: " + id
                                ));

        // Removing project from Employee owning side
        if (project.getEmployees() != null) {

            for (Employee employee :
                    project.getEmployees()) {

                if (employee.getProjects() != null) {

                    employee.getProjects()
                            .removeIf(existingProject ->
                                    existingProject.getId()
                                            .equals(project.getId())
                            );

                    employeeRepository.save(employee);
                }
            }
        }

        projectRepository.delete(project);
    }


    // ENTITY → DTO

    public ProjectDTO convertToDTO(Project project) {

        ProjectDTO dto = new ProjectDTO();

        dto.setId(project.getId());
        dto.setName(project.getName());

        if (project.getEmployees() != null &&
                !project.getEmployees().isEmpty()) {

            dto.setEmployeeIds(
                    project.getEmployees()
                            .stream()
                            .map(Employee::getId)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }


    // DTO → ENTITY

    public Project convertToEntity(ProjectDTO dto) {

        Project project = new Project();

        project.setName(dto.getName());

        if (dto.getEmployeeIds() != null &&
                !dto.getEmployeeIds().isEmpty()) {

            List<Employee> employees =
                    employeeRepository.findAllById(
                            dto.getEmployeeIds()
                    );

            project.setEmployees(employees);
        }

        return project;
    }
}