package com.employees.employeemanagement.controller;

import com.employees.employeemanagement.dto.ProjectDTO;
import com.employees.employeemanagement.entity.Project;
import com.employees.employeemanagement.service.ProjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    // CREATE
    @PostMapping
    public ProjectDTO addProject(@RequestBody ProjectDTO projectDTO) {

        Project project =
                projectService.convertToEntity(projectDTO);

        Project savedProject =
                projectService.saveProject(project);

        return projectService.convertToDTO(savedProject);
    }

    // GET ALL
    @GetMapping("/all")
    public List<ProjectDTO> getAllProjects() {
        return projectService.getAllProjects();
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ProjectDTO getProjectById(@PathVariable Integer id) {
        return projectService.getProjectById(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ProjectDTO updateProject(
            @PathVariable Integer id,
            @RequestBody ProjectDTO projectDTO) {

        return projectService.updateProject(id, projectDTO);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String deleteProject(@PathVariable Integer id) {

        projectService.deleteProject(id);

        return "Project deleted successfully!";
    }
}