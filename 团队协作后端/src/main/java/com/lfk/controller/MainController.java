package com.lfk.controller;



import com.google.gson.Gson;
import com.lfk.model.UserEntity;
import com.lfk.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    // 自动装配数据库接口，不需要再写原始的Connection来操作数据库
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    //注册
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public void register(HttpServletRequest request,HttpServletResponse respose){


    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void getJson(HttpServletRequest req, HttpServletResponse rep) throws Exception {
        PrintWriter writer = rep.getWriter();
        List<UserEntity> userList = userRepository.findAll();
        Gson gson = new Gson();
        String s=gson.toJson(userList);
        writer.println(s);
        writer.flush();
        writer.close();
    }

    @RequestMapping(value = "/admin/user", method = RequestMethod.GET)
    public String getUsers(ModelMap modelMap) {
        // 查询user表中所有记录
        List<UserEntity> userList = userRepository.findAll();

        // 将所有记录传递给要返回的jsp页面，放在userList当中
        modelMap.addAttribute("userList", userList);

        // 返回pages目录下的admin/users.jsp页面
        return "admin/user";

    }
}
