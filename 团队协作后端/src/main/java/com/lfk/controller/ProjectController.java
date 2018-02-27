package com.lfk.controller;

import com.lfk.model.MemberEntity;
import com.lfk.model.ProjectEntity;
import com.lfk.model.UserEntity;
import com.lfk.repository.MemberRepository;
import com.lfk.repository.ProjectRepository;
import com.lfk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/project")
public class ProjectController {

    // 自动装配数据库接口，不需要再写原始的Connection来操作数据库
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MemberRepository memberRepository;

    //获取自己创建的项目
    @RequestMapping(value = "/getMyProjects",method = RequestMethod.POST)
    @ResponseBody
    public Map getPros(@RequestBody UserEntity u){
        Map map=new HashMap();
        UserEntity userTemp=userRepository.findByUserName(u.getUserName());
        List list=projectRepository.findByUserByUserId(userTemp.getId());
        if(list!=null) {
            map.put("result",1);
            map.put("data", list);
            return map;
        }
        map.put("result",0);
        return map;
    }

    //获取自己参与的项目（非创建者）
    @RequestMapping(value="getJoinedProjects",method = RequestMethod.POST)
    @ResponseBody
    public Map getJoinedProjects(@RequestBody UserEntity u){
        Map map=new HashMap();
        int userId=userRepository.findByUserName(u.getUserName()).getId();
        List<MemberEntity> memberList=memberRepository.findByUserId(userId);
        List<ProjectEntity> data=new ArrayList<>();
        if(memberList!=null) {
            for (MemberEntity m : memberList
                    ) {
                ProjectEntity p = projectRepository.findOne(m.getProjectByProId().getId());
                data.add(p);
            }
            map.put("result", 1);
            map.put("data", data);
            return map;
        }
        map.put("data",data);
        map.put("result",0);
        return map;
    }

    //对项目进行操作时客户端所需传递的参数
    class ProjectOperateParam{
        public UserEntity user;
        public ProjectEntity project;
    }

    //添加项目
    @RequestMapping(value = "/addProject",method = RequestMethod.POST)
    @ResponseBody
    public Map addProject(@RequestBody ProjectOperateParam param){
        Map map=new HashMap();
        //System.out.println(project.getUserByUserId().getUserName());
        int id=(userRepository.findByUserName(param.user.getUserName())).getId();
        UserEntity userTemp=new UserEntity();
        userTemp.setId(id);
        param.project.setUserByUserId(userTemp);
        param.project.getUserByUserId().setId(id);
        projectRepository.saveAndFlush(param.project);

        //MemberEntity member=new MemberEntity();
        map.put("result",1);
        return map;
    }


    //删除项目
    @RequestMapping(value="/deleteProject",method=RequestMethod.POST)
    @ResponseBody
    public Map deleteProject(@RequestBody ProjectOperateParam param){
        Map map=new HashMap();
        UserEntity user=userRepository.findByUserName(param.user.getUserName());
        if(user!=null) {
            ProjectEntity p=projectRepository.findOne(param.project.getId());
            if(p!=null && p.getUserByUserId().getId() == user.getId()) {
                projectRepository.delete(param.project.getId());
                map.put("result",1);
                return map;
            }
        }
        map.put("result",0);
        return map;
    }


    //修改项目
    @RequestMapping(value = "/updateProject",method = RequestMethod.POST)
    @ResponseBody
    public Map updateProject(@RequestBody ProjectOperateParam param){
        Map map=new HashMap();
        UserEntity user=userRepository.findByUserName(param.user.getUserName());
        ProjectEntity pro=projectRepository.findOne(param.project.getId());
        MemberEntity m=memberRepository.findByUserIdAndProId(user.getId(),param.project.getId());
        if(pro.getUserByUserId().getId()==user.getId()){
            projectRepository.updateProject(param.project.getProjectName(), param.project.getProDescription(),param.project.getDeadline(), pro.getId());
            map.put("result",1);
            return map;
        }
        if(m!=null ) {
            if(m.getAuthority()==1) {
                projectRepository.updateProject(param.project.getProjectName(), param.project.getProDescription(),param.project.getDeadline(), pro.getId());
                map.put("result",1);
                return map;
            }
        }
        map.put("result",0);
        return map;
    }
}
