package com.jjunpro.shop.enums;

public enum DomainType implements EnumModel {

    NULL("null"),
    ACCOUNT("account"),
    PRODUCT("product"),
    PRODUCTORDER("productOrder");

    public final String domainType;

    DomainType(String domainType) {
        this.domainType = domainType;
    }

    @Override
    public String getKey() {
        return name();
    }

    @Override
    public String getValue() {
        return this.domainType;
    }
}