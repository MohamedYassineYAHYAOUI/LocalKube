package fr.umlv.LocalKube.demo;

import java.util.Objects;

public class Demo {

    private final int id;
    private final String name;

    public Demo(int id, String name){
        this.name = Objects.requireNonNull(name);
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
