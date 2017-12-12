package com.lfk.model;

import javax.persistence.*;

@Entity
@Table(name = "subtask", schema = "test", catalog = "")
public class SubtaskEntity {
    private int id;
    private String subTaskName;
    private short isFinished;
    private TaskEntity taskByTaskId;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "SubTaskName", nullable = true, length = 50)
    public String getSubTaskName() {
        return subTaskName;
    }

    public void setSubTaskName(String subTaskName) {
        this.subTaskName = subTaskName;
    }

    @Basic
    @Column(name = "IsFinished", nullable = false)
    public short getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(short isFinished) {
        this.isFinished = isFinished;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubtaskEntity that = (SubtaskEntity) o;

        if (id != that.id) return false;
        if (isFinished != that.isFinished) return false;
        if (subTaskName != null ? !subTaskName.equals(that.subTaskName) : that.subTaskName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (subTaskName != null ? subTaskName.hashCode() : 0);
        result = 31 * result + (int) isFinished;
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "TaskID", referencedColumnName = "ID", nullable = false)
    public TaskEntity getTaskByTaskId() {
        return taskByTaskId;
    }

    public void setTaskByTaskId(TaskEntity taskByTaskId) {
        this.taskByTaskId = taskByTaskId;
    }
}
