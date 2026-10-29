package com.rest.repository;

import com.rest.models.table;
import java.util.ArrayList;
import java.util.List;

public class tableRepository {
    private final List<table> tables = new ArrayList<>();

    public tableRepository(){
        tables.add(new table(1,2));
        tables.add(new table(2,4));
        tables.add(new table(3,6));
    }

    public List<table> findAll(){
        return tables;
    }

    public table findById(int id){
        for(table t : tables){
            if(t.getId() == id){
                return t;
            }
        }
        return null;
    }
}
