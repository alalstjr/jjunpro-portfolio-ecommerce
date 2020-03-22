package com.jjunpro.shop.config;

import com.jjunpro.shop.Application;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackageClasses = Application.class)
public class DataSourceConfig {

}