package com.arms.api.engine.model.enums;

public enum IsReqType {
    ALL,
    REQUIREMENT,
    ISSUE;

    public Boolean isReq(){
        if (this==REQUIREMENT){
            return true;
        };
        return false;
    }

    public Boolean isAll(){
        if (this==ALL){
            return true;
        }
        return false;
    }

    public Boolean isNotAllAndIsReq(){
        return !isAll()&&isReq();
    }

}
