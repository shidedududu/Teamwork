package com.lfk.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "project", schema = "test", catalog = "")
public class ProjectEntity {
    private int id;
    private String proDescription;
    private transient Collection<MemberEntity> membersById;
    private UserEntity userByUserId;
    private transient Collection<TaskEntity> tasksById;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "ProDescription", nullable = true, length = 150)
    public String getProDescription() {
        return proDescription;
    }

    public void setProDescription(String proDescription) {
        this.proDescription = proDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectEntity that = (ProjectEntity) o;

        if (id != that.id) return false;
        if (proDescription != null ? !proDescription.equals(that.proDescription) : that.proDescription != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (proDescription != null ? proDescription.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "projectByProId")
    public Collection<MemberEntity> getMembersById() {
        return membersById;
    }

    public void setMembersById(Collection<MemberEntity> membersById) {
        this.membersById = membersById;
    }

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "ID", nullable = false)
    public UserEntity getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(UserEntity userByUserId) {
        this.userByUserId = userByUserId;
    }

    @OneToMany(mappedBy = "projectByProId")
    public Collection<TaskEntity> getTasksById() {
        return tasksById;
    }

    public void setTasksById(Collection<TaskEntity> tasksById) {
        this.tasksById = tasksById;
    }
}
