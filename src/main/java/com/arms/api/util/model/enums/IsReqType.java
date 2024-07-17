package com.arms.api.util.model.enums;

public enum IsReqType {
    ALL,
    REQUIREMENT,
    ISSUE;

    public static Boolean isReqOrGetNull(IsReqType isReqType){
        if(isReqType == null || isReqType == ALL){
            return null;
        }
        if (isReqType==REQUIREMENT){
            return true;
        };
        return false;
    }

    public static Boolean isReq(IsReqType isReqType){
        if(isReqType == null){
            return false;
        }
        if (isReqType==REQUIREMENT){
            return true;
        };
        return false;
    }

    public static Boolean isIssue(IsReqType isReqType){
        return !isReq(isReqType);
    }



}
