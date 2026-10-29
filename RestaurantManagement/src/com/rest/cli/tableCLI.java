package com.rest.cli;

import com.rest.models.table;
import com.rest.services.tableService;
import java.util.List;
import java.util.Scanner;

public class tableCLI {

    private final tableService service;
    private final Scanner scanner;

    public tableCLI(tableService service){
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public tableCLI(tableService service, Scanner scanner){
        this.service = service;
        this.scanner = scanner;
    }

    public void start(){
        while(true){
            System.out.println("\n==RESTAURANT TABLE MANAGEMENT==");
            System.out.println("1. View all tables.");
            System.out.println("2. Back to Main Menu");

            System.out.print("Enter Choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if(choice == 1){
                List<table> tables = service.getAllTables();

                System.out.println("\n==ALL TABLES==");

                for(table t : tables){
                    System.out.println(t);
                }
            }else if(choice == 2 || choice == 0){
                break;
            }else{
                System.out.println("Invalid Choice");
            }
        }
    }
}
