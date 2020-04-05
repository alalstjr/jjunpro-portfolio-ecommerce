package com.jjunpro.shop.enums;

public enum DomainType implements EnumModel {

    ACCOUNT("account"),
    PRODUCT("product");

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