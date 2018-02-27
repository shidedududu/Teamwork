package com.lfk.controller;



import com.google.gson.Gson;
import com.lfk.model.UserEntity;
import com.lfk.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    // 自动装配数据库接口，不需要再写原始的Connection来操作数据库
    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    //测试
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    @ResponseBody
    public Map test(HttpServletRequest request, HttpServletResponse respose){
        Map map=new HashMap();
        map.put("result",1);
        return map;
    }

    @RequestMapping(value="/duplicationCheck",method = RequestMethod.POST)
    @ResponseBody
    public Map duplivationCheck(@RequestBody UserEntity u) {
        Map map = new HashMap();
        if (userRepository.findByUserName(u.getUserName()) == null) {
            map.put("result",1);
            return map;
        }
        map.put("result",0);
        return map;
    }

    //注册
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    @ResponseBody
    public Map register(@RequestBody UserEntity u, HttpServletRequest request, HttpServletResponse respose){
        Map map=new HashMap();
        //String userName=request.getParameter("userName");
        //String userPassword=request.getParameter("userPassword");
        //System.out.println(userName+" "+userPassword);
        if(userRepository.findByUserName(u.getUserName())==null){
            userRepository.saveAndFlush(u);
            map.put("result",1);
            return map;
        }
        map.put("result",0);
        return map;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Map login(@RequestBody UserEntity u, HttpServletRequest req, HttpServletResponse rep) throws Exception {
        Map map=new HashMap();
       // String userName=req.getParameter("userName");
        //String userPassword=req.getParameter("userPassword");
        String realPassword=null;
        if(userRepository.findByUserName(u.getUserName())!=null) {
            realPassword = userRepository.findByUserName(u.getUserName()).getUserPassword();
            if (u.getUserPassword().equals(realPassword)) {
                map.put("result", 1);
                return map;
            }
        }
        map.put("result",0);
        return map;
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
