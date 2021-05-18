package com.example.onetimechat.ui.main;

public class Session {

    private String token;
    private static Session instance = null;

    protected Session() {
        this.token = "";
    }
    public String getToken(){
        return this.token;
    }
    public void setToken(String token){
        this.token = token;
    }

    public static Session getInstance() {
        if(instance == null) {
            instance = new Session();
        }
        return instance;
    }


}
