package com.example.asd.sqlutils;

import android.database.Cursor;

import com.example.asd.models.subtask.SubTaskEntity;

import java.util.List;

import static com.example.asd.testapp.MainActivity.DB;

/**
 * Created by asd on 2017/12/27.
 */

public class SubTaskUtil {
    public static void refreshTableSubTask(List<SubTaskEntity> subtask,String taskId){
        try {
            DB.execSQL("delete from subtask");
            subTaskDataSave(subtask,taskId);
        }catch(Exception e){
            tableSubtaskCreate();
            subTaskDataSave(subtask,taskId);
        }
    }

    public static void subTaskDataSave(List<SubTaskEntity> subtask,String taskId){
        if(subtask!=null){
            for (SubTaskEntity st:subtask
                 ) {
                subtaskdataInsert(String.valueOf(st.id),taskId,st.subTaskName,String.valueOf(st.isFinished));
            }
        }
    }

    public static void subtaskdataInsert(String Id,String TaskID,String SubtaskName, String IsFinished) {
        String insertSql="insert into subtask(_id,TaskID,SubTaskName,IsFinished) " +
                "values(?,?,?,?)";
        DB.execSQL(insertSql,new String[]{Id,TaskID,SubtaskName,IsFinished});
    }

    public static Cursor select(String ProID) {
        String selectSql = "select _id,TaskName from task where ProID=?";
        Cursor cursor = DB.rawQuery(selectSql, new String[]{ProID});// 我们需要查处所有项故不需要查询条件
        return cursor;
    }

    public static Cursor selectByTaskID(String TaskID) {
        String selectSql = "select _id,SubTaskName,IsFinished from subtask where TaskID=?";
        Cursor cursor = DB.rawQuery(selectSql, new String[]{TaskID});// 我们需要查所属任务的全部子任务
        return cursor;
    }

    public static Cursor selectBySubtaskID(String SubtaskID) {
        String selectSql = "select _id,SubTaskName,IsFinished from subtask where _id=?";
        Cursor cursor = DB.rawQuery(selectSql, new String[]{SubtaskID});// 我们需要点击所处的子任务位置信息
        return cursor;
    }

    //子任务表
    public static void tableSubtaskCreate(){
        String createSql= "create table subtask(_id integer primary key autoincrement,TaskID," +
                "SubTaskName,IsFinished)";
        DB.execSQL(createSql);
    }
}
