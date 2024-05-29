package com.arms.egovframework.javaservice.esframework.model.vo;

public class 검색조건_페이징 {
    private static final int DEFAULT_SIZE = 100;

    private int page;
    private int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size != 0 ? size : DEFAULT_SIZE;
    }

    public void setSize(int size) {
        this.size = size;
    }
}