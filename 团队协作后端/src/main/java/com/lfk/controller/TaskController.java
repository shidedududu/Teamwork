package com.lfk.controller;

import com.lfk.model.*;
import com.lfk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/task")
public class TaskController {

    @Autowired
    TaskRepository taskRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ExecutorRepository executorRepository;

    private class TaskParam{
        public UserEntity user;
        public ProjectEntity project;
        public TaskEntity task;
    }

    private class GetTaskParam{
        public UserEntity user;
        public ProjectEntity project;
    }

    //查询任务（普通用户仅查看自己被分配到的任务）
    @RequestMapping(value="/getTasks",method = RequestMethod.POST)
    @ResponseBody
    public Map getTasks(GetTaskParam param){
        Map map=new HashMap();
        ProjectEntity pro=projectRepository.findOne(param.project.getId());
        MemberEntity m=memberRepository.findByUserIdAndProId(param.user.getId(),param.project.getId());
        List<TaskEntity> taskList=new ArrayList<>();
       if(pro == null){
           map.put("result",0);
       }
       else if(pro.getUserByUserId().getId()==param.user.getId()){
           map.put("result",1);
           List listTemp=taskRepository.findByProId(pro.getId());
           taskList.addAll(listTemp);
           map.put("data",taskList);
       }
       else if(m != null){
           map.put("result",1);
           if(m.getAuthority()==1){
               List listTemp=taskRepository.findByProId(pro.getId());
               taskList.addAll(listTemp);
               map.put("data",taskList);
           }
           else{
               List listTemp=taskRepository.findByProId(pro.getId());
               if(listTemp==null){
                   map.put("data",taskList);
               }
               else{
                   for (TaskEntity t:taskList
                        ) {
                       ExecutorEntity e=executorRepository.findByTaskByTaskIdAndUserByUserId(t.getId(),param.user.getId());
                       if(e!=null){
                               taskList.add(t);
                       }
                   }
                   map.put("result",taskList);
               }
           }
       }
        return map;
    }



    //创建任务
    @RequestMapping(value="/addTask",method = RequestMethod.POST)
    @ResponseBody
    public Map addTask(@RequestBody TaskParam param){
        Map map=new HashMap();
        ProjectEntity pro=projectRepository.findOne(param.project.getId());
        MemberEntity mem=memberRepository.findByUserIdAndProId(param.user.getId(),param.project.getId());
        if(pro!=null){
            if((mem!=null && mem.getAuthority()==1) || pro.getUserByUserId().getId() == param.user.getId()){
                taskRepository.saveAndFlush(param.task);
                map.put("result",1);
                return map;
            }
        }
        map.put("result",0);
        return map;
    }

    //修改任务,未完成
    @RequestMapping(value="/updateTask",method = RequestMethod.POST)
    @ResponseBody
    public Map updateTask(@RequestBody TaskParam param){
        Map map=new HashMap();
        ProjectEntity project=projectRepository.findOne(param.project.getId());
        if(project!=null){

        }
        return map;
    }

    //删除任务
    @RequestMapping(value="/deleteTask",method=RequestMethod.POST)
    @ResponseBody
    public Map deleteTask(@RequestBody TaskParam param){
        Map map=new HashMap();
        ProjectEntity pro=projectRepository.findOne(param.project.getId());
        MemberEntity mem=memberRepository.findByUserIdAndProId(param.user.getId(),param.project.getId());
        if(pro!=null){
            if((mem!=null && mem.getAuthority()==1) || pro.getUserByUserId().getId() == param.user.getId()){
                if(taskRepository.findOne(param.task.getId())!=null){
                    taskRepository.delete(taskRepository.findOne(param.task.getId()));
                    map.put("result",1);
                    return map;
                }
            }
        }
        map.put("result",0);
        return map;
    }

    private class ExecutorParam{
        UserEntity user;
        ProjectEntity project;
        TaskEntity task;
        UserEntity executor;
    }
    //设置任务执行者
    @RequestMapping(value="/setExecutor",method = RequestMethod.POST)
    @ResponseBody
    public Map setExecutor(@RequestBody ExecutorParam param){
        Map map=new HashMap();
        UserEntity user=userRepository.findByUserName(param.user.getUserName());
        if((param.project.getUserByUserId().getId() == user.getId())
                || memberRepository.findByUserIdAndProId(user.getId(),param.project.getId()).getAuthority()==1){
            ExecutorEntity e=new ExecutorEntity();
            e.setTaskByTaskId(param.task);
            e.setUserByUserId(user);
            executorRepository.saveAndFlush(e);
            map.put("result",1);
            return map;
        }
        map.put("result",0);
        return map;
    }


    //删除任务执行者
    @RequestMapping(value="/deleteExecutor",method = RequestMethod.POST)
    @ResponseBody
    public Map deleteExecutor(@RequestBody ExecutorParam param){
        Map map=new HashMap();
        UserEntity user=userRepository.findByUserName(param.user.getUserName());
        if((param.project.getUserByUserId().getId() == user.getId())
                || memberRepository.findByUserIdAndProId(user.getId(),param.project.getId()).getAuthority()==1) {
            ExecutorEntity e=executorRepository.findByTaskByTaskIdAndUserByUserId(param.task.getId(),param.executor.getId());
            if(e!=null) {
                executorRepository.delete(e);
                map.put("result",1);
                return map;
            }
        }
        map.put("result",0);
        return map;
    }
}
