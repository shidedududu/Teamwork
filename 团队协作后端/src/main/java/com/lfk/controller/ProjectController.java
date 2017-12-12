package com.lfk.controller;

import com.lfk.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectController {
    // 自动装配数据库接口，不需要再写原始的Connection来操作数据库
    @Autowired
    ProjectRepository projectRepository;
}
