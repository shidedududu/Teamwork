package com.lfk.controller;

import com.lfk.model.MemberEntity;
import com.lfk.model.ProjectEntity;
import com.lfk.model.UserEntity;
import com.lfk.repository.MemberRepository;
import com.lfk.repository.ProjectRepository;
import com.lfk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/member")
public class MemberController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProjectRepository projectRepository;

    //搜索用户
    @RequestMapping(value = "/findUser", method = RequestMethod.POST)
    @ResponseBody
    public Map findUser(@RequestBody UserEntity user) {
        Map map = new HashMap();
        if (userRepository.findByUserName(user.getUserName()) != null) {
            map.put("result", 1);
            return map;
        }
        map.put("result", 0);
        return map;
    }

    class MemberParam {
        UserEntity user;
        UserEntity member;
        ProjectEntity projectEntity;
    }

    //添加成员
    @RequestMapping(value = "/addMember", method = RequestMethod.POST)
    @ResponseBody
    public Map addMember(@RequestBody MemberParam param) {
        Map map = new HashMap();
        UserEntity user = userRepository.findByUserName(param.user.getUserName());
        ProjectEntity project = projectRepository.findOne(param.projectEntity.getId());
        if (project != null) {
            if (project.getUserByUserId().getId() == user.getId() || memberRepository.findByUserIdAndProId(user.getId(), project.getId())
                    .getAuthority() == 1) {
                MemberEntity member = new MemberEntity();
                member.setUserByUserId(userRepository.findByUserName(param.member.getUserName()));
                member.setProjectByProId(project);
                member.setAuthority(2);
                memberRepository.saveAndFlush(member);
                map.put("result", 1);
                return map;
            }
        }
        map.put("result", 0);
        return map;
    }

    //删除人员
    class DeleteMemberParam{
        UserEntity user;
        ProjectEntity projectEntity;
        MemberEntity member;
    }
    @RequestMapping(value = "/deleteMember", method = RequestMethod.POST)
    @ResponseBody
    public Map deleteMember(@RequestBody DeleteMemberParam param) {
        Map map = new HashMap();
        UserEntity user = userRepository.findByUserName(param.user.getUserName());
        ProjectEntity project = projectRepository.findOne(param.projectEntity.getId());
        //UserEntity member = userRepository.findByUserName(param.member.getUserName());
        if (project != null) {
            if (project.getUserByUserId().getId() == user.getId() || memberRepository.findByUserIdAndProId(user.getId(), project.getId())
                    .getAuthority() == 1) {
                MemberEntity memberEntity = memberRepository.findOne(param.member.getId());
                if (memberEntity != null) {
                    memberRepository.delete(memberEntity);
                    map.put("result", 1);
                    return map;
                }
            }
        }
        map.put("result", 0);
        return map;
    }

    //获取成员
    @RequestMapping(value="/getMembers",method = RequestMethod.POST)
    @ResponseBody
    public Map getMember(@RequestBody ProjectEntity project){
        Map map=new HashMap();
        List<UserEntity> list=new ArrayList<>();
        List<Integer> memberId=new ArrayList<>();
        if(projectRepository.findOne(project.getId())!=null){
            List<MemberEntity> temp=memberRepository.findByProId(project.getId());

            if(temp!=null){
                for (MemberEntity m:temp
                     ) {
                    list.add(userRepository.findOne(m.getUserByUserId().getId()));
                    memberId.add(m.getId());
                }
            }
            map.put("result",1);
            map.put("data",list);
            map.put("member",memberId);
            return map;
        }
        map.put("result",0);
        return map;
    }

    //获取项目拥有者
    @RequestMapping(value="/getProMaster",method = RequestMethod.POST)
    @ResponseBody
    public Map getProMaster(@RequestBody ProjectEntity project){
        Map map=new HashMap();
        if(projectRepository.findOne(project.getId())!=null){
            UserEntity user=new UserEntity();
            user=userRepository.findOne(projectRepository.findOne(project.getId()).getUserByUserId().getId());
            map.put("result",1);
            map.put("data",user);
            return map;
        }
        map.put("result",0);
        return map;
    }

    class UpadateAutorityParam {
        UserEntity user;
        UserEntity member;
        ProjectEntity project;
        int authority;
    }

    //设置用户权限
    @RequestMapping(value = "/setAuthority", method = RequestMethod.POST)
    @ResponseBody
    public Map setAuthority(@RequestBody UpadateAutorityParam param) {
        Map map = new HashMap();
        UserEntity user = userRepository.findByUserName(param.user.getUserName());
        UserEntity member = userRepository.findByUserName(param.member.getUserName());
        ProjectEntity project = projectRepository.findOne(param.project.getId());
        if (project != null) {
            if (project.getUserByUserId().getId() == user.getId() || memberRepository.findByUserIdAndProId(user.getId(), project.getId())
                    .getAuthority() == 1) {
                MemberEntity memberEntity = memberRepository.findByUserIdAndProId(member.getId(), project.getId());
                if (memberEntity != null) {
                    memberRepository.updateAuthority(param.authority, memberEntity.getId());
                    map.put("result", 1);
                    return map;
                }
            }

        }
        map.put("result", 0);
        return map;
    }
}

