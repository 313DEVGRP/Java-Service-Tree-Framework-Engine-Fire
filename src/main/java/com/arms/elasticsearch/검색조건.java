package com.arms.elasticsearch;


import org.elasticsearch.search.sort.SortOrder;

import java.util.List;

public class 검색조건 extends 검색조건_페이징 {

    private List<String> fields;
    private String searchTerm;
    private String sortBy;
    private SortOrder order;

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }
}