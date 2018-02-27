package com.example.asd.sqlutils;

import android.database.Cursor;

import com.example.asd.models.task.TaskEntity;

import java.util.List;

import static com.example.asd.testapp.MainActivity.DB;

/**
 * Created by asd on 2017/12/26.
 */

public class TaskSqlUtil {
    public static Cursor selectByProID(String ProID) {
        String selectSql = "select _id,TaskName,Deadline,Completion from task where ProID=?";
        Cursor cursor;
        try {
           cursor = DB.rawQuery(selectSql, new String[]{ProID});// 我们需要查所属项目的全部任务
        }catch (Exception e){
            tableTaskCreate();
            cursor=DB.rawQuery(selectSql, new String[]{ProID});// 我们需要查所属项目的全部任务
        }
        return cursor;
    }

    public static void refreshTablcTask(List<TaskEntity> task,String proId){
        try {
            DB.execSQL("delete from task");
            taskDataSave(task,proId);
        }catch(Exception e){
            tableTaskCreate();
            taskDataSave(task,proId);
        }
    }

    public static void taskDataSave(List<TaskEntity> task,String proId){

        if(task!=null){
            for (TaskEntity t:task
                 ) {
                taskdataInsert(t.id,t.taskName,proId,t.deadline,t.taskDesciption,t.completion);
            }
        }
    }

    public static void tableTaskCreate(){
        String createSql= "create table task(_id integer primary key autoincrement, TaskName,ProID," +
                "Deadline,TaskDescription,Completion)";
        DB.execSQL(createSql);
    }

    public static void taskdataInsert(int id,String taskName,String ProID ,String deadline,String taskDescription,double Completion) {
        String insertSql="insert into task(_id,TaskName,ProID,Deadline,TaskDescription,Completion) " +
                "values(?,?,?,?,?,?)";
        DB.execSQL(insertSql,new String[]{String.valueOf(id),taskName,ProID,deadline,taskDescription,String.valueOf(Completion)});
    }

    public static Cursor select() {
        String selectSql = "select _id,ProjectName,DeadlineTime from project";
        Cursor cursor = DB.rawQuery(selectSql, null);// 我们需要查处所有项故不需要查询条件
        return cursor;
    }
}
