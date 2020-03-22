package com.jjunpro.shop.mapper;

import com.jjunpro.shop.model.Account;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {

    Account selectAccountById(Long id);

    List<Account> selectAllAccount();

    void insertAccount(Account city);
}