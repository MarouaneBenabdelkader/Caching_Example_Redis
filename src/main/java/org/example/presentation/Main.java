package org.example.presentation;


import org.example.buisiness.DefaultServices;
import org.example.buisiness.Services;
import org.example.dao.CachedDataManagerSql;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CachedDataManagerSql cdatamanager = new CachedDataManagerSql();
        DefaultServices services = Services.getInstance();
        while(true){
            System.out.println("Enter client id: ");
            Scanner scanner = new Scanner(System.in);
            String id_client = scanner.nextLine();
            services.dataSourceManager(id_client);
            System.out.println("Enter 1 to continue or 0 to exit: ");
            int choice = scanner.nextInt();
            if(choice == 0){
                services.closeDbConncetion();
                break;
            }
        }
    }
}