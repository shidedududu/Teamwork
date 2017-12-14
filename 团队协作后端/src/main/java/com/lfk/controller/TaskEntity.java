package com.lfk.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "task", schema = "test", catalog = "")
public class TaskEntity {
    private int id;
    private Timestamp deadline;
    private String taskDesciption;
    private double completion;
    private transient Collection<ExecutorEntity> executorsById;
    private transient Collection<SubtaskEntity> subtasksById;
    private ProjectEntity projectByProId;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Deadline", nullable = true)
    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    @Basic
    @Column(name = "TaskDesciption", nullable = true, length = 150)
    public String getTaskDesciption() {
        return taskDesciption;
    }

    public void setTaskDesciption(String taskDesciption) {
        this.taskDesciption = taskDesciption;
    }

    @Basic
    @Column(name = "Completion", nullable = false, precision = 0)
    public double getCompletion() {
        return completion;
    }

    public void setCompletion(double completion) {
        this.completion = completion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskEntity that = (TaskEntity) o;

        if (id != that.id) return false;
        if (Double.compare(that.completion, completion) != 0) return false;
        if (deadline != null ? !deadline.equals(that.deadline) : that.deadline != null) return false;
        if (taskDesciption != null ? !taskDesciption.equals(that.taskDesciption) : that.taskDesciption != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (deadline != null ? deadline.hashCode() : 0);
        result = 31 * result + (taskDesciption != null ? taskDesciption.hashCode() : 0);
        temp = Double.doubleToLongBits(completion);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @OneToMany(mappedBy = "taskByTaskId")
    public Collection<ExecutorEntity> getExecutorsById() {
        return executorsById;
    }

    public void setExecutorsById(Collection<ExecutorEntity> executorsById) {
        this.executorsById = executorsById;
    }

    @OneToMany(mappedBy = "taskByTaskId")
    public Collection<SubtaskEntity> getSubtasksById() {
        return subtasksById;
    }

    public void setSubtasksById(Collection<SubtaskEntity> subtasksById) {
        this.subtasksById = subtasksById;
    }

    @ManyToOne
    @JoinColumn(name = "ProID", referencedColumnName = "ID", nullable = false)
    public ProjectEntity getProjectByProId() {
        return projectByProId;
    }

    public void setProjectByProId(ProjectEntity projectByProId) {
        this.projectByProId = projectByProId;
    }
}
