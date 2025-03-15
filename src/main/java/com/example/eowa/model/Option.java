package com.example.eowa.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Option{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "optionvalue")
    private String value;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> voters;

    @Column
    private boolean selected;

    public Option() {
    }

    public Option(String value,Set<User> voters, boolean selected) {
        this.value = value;
        this.voters = voters;
        this.selected = selected;
    }

    public Option(Option other){
        this(other.getValue());
    }

    public Option(String value) {
        this(value,new HashSet<>(),false);
    }


    public long getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Set<User> getVoters() {
        return voters;
    }

    public void setVoters(Set<User> voters) {
        this.voters = voters;
    }

    public void addVoter(User voter){
        this.voters.add(voter);
    }

    public void removeVoter(User voter){
        this.voters.remove(voter);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Opinion)){
            return false;
        }
        return ((Opinion) obj).getId() == this.getId();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
