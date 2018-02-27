package com.example.asd.sqlutils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.asd.models.project.ProjectEntity;
import com.example.asd.testapp.ProjectFragment;

import java.util.List;

import static com.example.asd.testapp.MainActivity.DB;

/**
 * Created by asd on 2017/12/26.
 */

public class ProjectSqlUtil {
    public static void refreshTableProject(List<ProjectEntity> projects){
        try {
            DB.execSQL("delete from project");
            projectDataSave(projects);
        }catch(Exception e){
            tableMyProjectCreate();
            projectDataSave(projects);
        }
    }

    public static void refreshTableOtherProject(List<ProjectEntity> projects){
        try {
            DB.execSQL("delete from otherproject");
            otherProjectDataSave(projects);
        }catch(Exception e){
            tableOtherProjectCreate();
            otherProjectDataSave(projects);
        }
    }

    //数据库更新
    public static void projectDataSave(List<ProjectEntity> projects){
        if(projects!=null){
            for (ProjectEntity p:projects
                 ) {
                myProjectDataInsert(p.id,p.projectName,p.deadline,p.proDescription);
            }
        }
    }

    public static void otherProjectDataSave(List<ProjectEntity> projects){
        if(projects!=null){
            for (ProjectEntity p:projects
                    ) {
                otherProjectDataInsert(p.id,p.projectName,p.deadline,p.proDescription);
            }
        }
    }

    //数据库操作部分
    public static void myProjectDataInsert(int proId,String projectName, String deadline,String projectDescription) {
        String insertSql="insert into project(_id,ProjectName,DeadlineTime,ProjectDescription) " +
                "values(?,?,?,?)";
        String projectId=String.valueOf(proId);
        DB.execSQL(insertSql,new String[]{projectId,projectName,deadline,projectDescription});
    }

    public static void tableMyProjectCreate(){
        String createSql= "create table project(_id integer primary key autoincrement, ProjectName," +
                "DeadlineTime,ProjectDescription)";
        DB.execSQL(createSql);
    }

    public static Cursor selectFromMyProject() {
        String selectSql = "select _id,ProjectName,DeadlineTime from project";
        Cursor cursor = DB.rawQuery(selectSql, null);// 我们需要查处所有项故不需要查询条件
        return cursor;
    }

    public static void tableOtherProjectCreate(){
        String createSql= "create table otherproject(_id integer primary key autoincrement, ProjectName," +
                "DeadlineTime,ProjectDescription)";
        DB.execSQL(createSql);
    }

    public static void otherProjectDataInsert(int proId,String projectName, String deadline,String projectDescription) {
        String insertSql="insert into otherproject(_id,ProjectName,DeadlineTime,ProjectDescription) " +
                "values(?,?,?,?)";
        DB.execSQL(insertSql,new String[]{String.valueOf(proId),projectName,deadline,projectDescription});
    }

    public static Cursor selectFromOtherProject() {
        String selectSql = "select _id,ProjectName,DeadlineTime from otherproject";
        Cursor cursor ;
        try {
            cursor = DB.rawQuery(selectSql, null);// 我们需要查处所有项故不需要查询条件
        }catch (Exception e){
            tableOtherProjectCreate();
            cursor=DB.rawQuery(selectSql, null);
        }
        return cursor;
    }
    public static Cursor select(String ProID) {
        String selectSql = "select ProjectName,DeadlineTime,ProjectDescription from project where _id=?";
        Cursor cursor = DB.rawQuery(selectSql, new String[]{ProID});// 我们需要项目ID所在项目
        return cursor;
    }

    public static Cursor selectFromOtherById(String id){
        String selectSql = "select ProjectName,DeadlineTime,ProjectDescription from otherproject where _id=?";
        Cursor cursor = DB.rawQuery(selectSql, new String[]{id});// 我们需要项目ID所在项目
        return cursor;
    }

}
