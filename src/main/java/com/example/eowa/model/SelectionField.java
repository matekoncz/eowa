package com.example.eowa.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class SelectionField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private boolean openForModification;

    @Column
    private boolean selectedByPoll;

    @OneToMany(fetch = FetchType.EAGER)
    private Set<Option> options;

    public SelectionField() {
    }

    public SelectionField(String title, boolean openForModification, boolean selectedByPoll, Set<Option> options) {
        this.title = title;
        this.openForModification = openForModification;
        this.selectedByPoll = selectedByPoll;
        this.options = options;
    }

    public SelectionField(String title, boolean openForModification, boolean selectedByPoll){
        this(title,openForModification,selectedByPoll,new HashSet<>());
    }

    public SelectionField(SelectionField other){
        this(
                other.title,
                other.openForModification,
                other.selectedByPoll,
                other.getOptions().stream()
                        .map(Option::new)
                        .collect(Collectors.toSet()));
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOpenForModification() {
        return openForModification;
    }

    public void setOpenForModification(boolean openForModification) {
        this.openForModification = openForModification;
    }

    public boolean isSelectedByPoll() {
        return selectedByPoll;
    }

    public void setSelectedByPoll(boolean selectedByPoll) {
        this.selectedByPoll = selectedByPoll;
    }

    public Set<Option> getOptions() {
        return options;
    }

    public void setOptions(Set<Option> options) {
        this.options = options;
    }

    public void addOptions(Set<Option> options) {
        this.options.addAll(options);
    }

    public void removeOptions(Set<Option> options){
        this.options.removeAll(options);
    }

    @Override
    public int hashCode() {
        return title.charAt(0);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SelectionField)){
            return false;
        }
        if(this == obj){
            return true;
        }

        return this.getId() == ((SelectionField) obj).getId() && this.getTitle().equals(((SelectionField) obj).getTitle());
    }
}