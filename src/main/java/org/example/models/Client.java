package org.example.models;

public class Client {
    private Integer no_client;
    private String name;
    private String adress;
    public Client(Integer no_client, String name, String adress) {
        this.no_client = no_client;
        this.name = name;
        this.adress = adress;
    }

    public Integer getNo_client() {
        return no_client;
    }

    public void setNo_client(Integer no_client) {
        this.no_client = no_client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}