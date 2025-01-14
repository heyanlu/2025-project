package com.qiuzhitech.onlineshopping_06.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserDemo {

    public int id;
    public String name;
    public String email;

    public UserDemo(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

}
