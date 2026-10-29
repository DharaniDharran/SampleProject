package com.rest.services;

import com.rest.models.table;
import com.rest.repository.tableRepository;
import java.util.List;

public class tableService {
    
    private final tableRepository repository;

    public tableService(){
        repository = new tableRepository();
    }

    public tableService(tableRepository repository){
        this.repository = repository;
    }

    public List<table> getAllTables(){
        return repository.findAll();
    }

    public table getTableById(int id){
        return repository.findById(id);
    }
}
