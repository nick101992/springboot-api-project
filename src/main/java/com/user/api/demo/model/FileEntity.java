package com.user.api.demo.model;


import javax.persistence.*;
@Entity
@Table(name="file_data")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String path;
    @Column
    private long size;
    @Column
    private String contentType;
    @Column
    private String username;

    public FileEntity() {
    }

    public FileEntity(String name, String path, long size, String contentType, String username) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.contentType = contentType;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
