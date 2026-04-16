package com.trackiq.backend.service;

import com.trackiq.backend.entity.Project;
import com.trackiq.backend.repository.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    // ✅ ADD THIS (FIX ERROR)
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}