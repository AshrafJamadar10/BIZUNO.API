package com.bizuno.dtos.main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResponse {
    private Integer status;
    private String message;
    private Object data;

    public CommonResponse(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public CommonResponse(){
        this.status = 0;
        this.message = "";
    }

    public CommonResponse(Integer status, Object data){
        this.status = status;
        this.data = data;
    }

    public CommonResponse(Integer status, String message, Object data){
        this.status = status;
        this.message = message;
        this.data = data;
    }
}