package com.lfk.controller;

import com.lfk.model.SubtaskEntity;
import com.lfk.model.TaskEntity;
import com.lfk.repository.SubtaskRepository;
import com.lfk.repository.TaskRepository;
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

class SubTaskParam{
    TaskEntity task;
    SubtaskEntity subTask;
}

@Controller
@RequestMapping(value="/subtask")
public class SubtaskController {
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    SubtaskRepository subtaskRepository;

    @RequestMapping(value="/addSubtask",method= RequestMethod.POST)
    @ResponseBody
    public Map addSubtask(@RequestBody SubTaskParam param){
        Map map=new HashMap();
        TaskEntity t=taskRepository.findOne(param.task.getId());
        if(t!=null){
            SubtaskEntity st=new SubtaskEntity();
            st.setSubTaskName(param.subTask.getSubTaskName());
            st.setIsFinished((short)0);
            st.setTaskByTaskId(taskRepository.findOne(t.getId()));
            subtaskRepository.saveAndFlush(st);
            List<SubtaskEntity> s=subtaskRepository.getSubtaskEntityByTaskId(t.getId());
            double count=0;
            double finishCount=0;
            for (SubtaskEntity sb:s
                    ) {
                count++;
                if(sb.getIsFinished()==1)
                    finishCount++;
            }
            double completion=(finishCount/count)*100.0;
            taskRepository.updateCompletion(completion,t.getId());
            map.put("result",1);
            return map;
        }
        map.put("result",0);
        return map;
    }

    @RequestMapping(value="/getSubtasks",method = RequestMethod.POST)
    @ResponseBody
    public Map getSubtasks(@RequestBody TaskEntity param){
        Map map=new HashMap();
        List<SubtaskEntity> list=new ArrayList();
        TaskEntity t=taskRepository.findOne(param.getId());
        if(t!=null){
            List<SubtaskEntity> temp=subtaskRepository.getSubtaskEntityByTaskId(param.getId());
            map.put("result",1);
            if(temp!=null){
                list=temp;
            }
            map.put("data",list);
            return map;
        }
        map.put("result",0);
        return map;
    }

    @RequestMapping(value="/deleteSubtask",method = RequestMethod.POST)
    @ResponseBody
    public Map deleteSubtask(@RequestBody SubtaskEntity param){
        Map map=new HashMap();
        SubtaskEntity subtaskEntity=subtaskRepository.findOne(param.getId());
        if(subtaskEntity!=null){
            int id=subtaskEntity.getTaskByTaskId().getId();
            subtaskRepository.delete(subtaskEntity.getId());
            List<SubtaskEntity> s=subtaskRepository.getSubtaskEntityByTaskId(id);
            double count=0;
            double finishCount=0;
            for (SubtaskEntity sb:s
                    ) {
                count++;
                if(sb.getIsFinished()==1)
                    finishCount++;
            }
            double completion=(finishCount/count)*100.0;
            taskRepository.updateCompletion(completion,id);
            map.put("result",1);
            return map;
        }
        map.put("result",0);
        return map;
    }

    @RequestMapping(value="/finishSubtask",method=RequestMethod.POST)
    @ResponseBody
    public Map finishSubtask(@RequestBody SubtaskEntity param){
        Map map=new HashMap();
        SubtaskEntity subtaskEntity=subtaskRepository.findOne(param.getId());
        if(subtaskEntity!=null){
            subtaskRepository.setFinished((short)param.getIsFinished(),subtaskEntity.getId());
            List<SubtaskEntity> s=subtaskRepository.getSubtaskEntityByTaskId(subtaskEntity.getTaskByTaskId().getId());
            double count=0;
            double finishCount=0;
            for (SubtaskEntity sb:s
                 ) {
                count++;
                if(sb.getIsFinished()==1)
                    finishCount++;
            }
            double completion=(finishCount/count)*100.0;
            taskRepository.updateCompletion(completion,subtaskEntity.getTaskByTaskId().getId());
            map.put("result",1);
            return map;
        }
        map.put("result",0);
        return map;
    }


}
