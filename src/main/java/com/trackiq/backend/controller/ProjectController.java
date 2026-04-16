package com.trackiq.backend.controller;

import com.trackiq.backend.entity.Project;
import com.trackiq.backend.service.ProjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.trackiq.backend.entity.Project;
import com.trackiq.backend.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // ✅ GET ALL PROJECTS
    @GetMapping
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    // ✅ CREATE PROJECT (🔥 THIS WAS MISSING)
    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    // ✅ DELETE PROJECT (optional but useful)
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}