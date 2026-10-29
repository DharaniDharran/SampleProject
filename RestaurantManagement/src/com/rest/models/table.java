package com.rest.models;

public class table {
    public enum TableStatus{
        AVAILABLE,
        OCCUPIED
    }

    private int id;
    private int capacity;
    private TableStatus Status;

    public table(int id, int capacity){
        this.id = id;
        this.capacity = capacity;
        this.Status = TableStatus.AVAILABLE;
    }

    public int getId(){
        return id;
    }

    public int getCapacity(){
        return capacity;
    }

    public TableStatus getStatus(){
        return Status;
    }

    public void freeTable(){
        this.Status = TableStatus.AVAILABLE;
    }

    public void occupyTable(){
        this.Status = TableStatus.OCCUPIED;
    }

    public String toString(){
        return "Table ID: "+id+", Capacity: "+capacity+", Status: "+Status;
    }
}
