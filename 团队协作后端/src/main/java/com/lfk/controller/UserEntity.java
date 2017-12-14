package com.lfk.model;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "user", schema = "test", catalog = "")
public class UserEntity {
    private int id;
    private String userName;
    private String userPassword;
    private transient Collection<ExecutorEntity> executorsById;
    private transient Collection<MemberEntity> membersById;
    private transient Collection<ProjectEntity> projectsById;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "UserName", nullable = false, length = 30)
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Basic
    @Column(name = "UserPassword", nullable = false, length = 20)
    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity that = (UserEntity) o;

        if (id != that.id) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        if (userPassword != null ? !userPassword.equals(that.userPassword) : that.userPassword != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (userPassword != null ? userPassword.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "userByUserId")
    public Collection<ExecutorEntity> getExecutorsById() {
        return executorsById;
    }

    public void setExecutorsById(Collection<ExecutorEntity> executorsById) {
        this.executorsById = executorsById;
    }

    @OneToMany(mappedBy = "userByUserId")
    public Collection<MemberEntity> getMembersById() {
        return membersById;
    }

    public void setMembersById(Collection<MemberEntity> membersById) {
        this.membersById = membersById;
    }

    @OneToMany(mappedBy = "userByUserId")
    public Collection<ProjectEntity> getProjectsById() {
        return projectsById;
    }

    public void setProjectsById(Collection<ProjectEntity> projectsById) {
        this.projectsById = projectsById;
    }
}
