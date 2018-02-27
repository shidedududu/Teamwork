package com.lfk.model;

import javax.persistence.*;

@Entity
@Table(name = "member", schema = "test", catalog = "")
public class MemberEntity {
    private int id;
    private int authority;//等级0、1、2分别代表项目拥有者、
    // 项目普通用户与管理员
    private ProjectEntity projectByProId;
    private UserEntity userByUserId;

    @Id
    @Column(name = "ID", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "Authority", nullable = false)
    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MemberEntity that = (MemberEntity) o;

        if (id != that.id) return false;
        if (authority != that.authority) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + authority;
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "ProID", referencedColumnName = "ID", nullable = false)
    public ProjectEntity getProjectByProId() {
        return projectByProId;
    }

    public void setProjectByProId(ProjectEntity projectByProId) {
        this.projectByProId = projectByProId;
    }

    @ManyToOne
    @JoinColumn(name = "UserID", referencedColumnName = "ID", nullable = false)
    public UserEntity getUserByUserId() {
        return userByUserId;
    }

    public void setUserByUserId(UserEntity userByUserId) {
        this.userByUserId = userByUserId;
    }
}
