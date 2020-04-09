package com.jjunpro.shop.converter;

import com.jjunpro.shop.dto.ReceiptDTO;
import org.springframework.stereotype.Component;
import org.springframework.core.convert.converter.Converter;

@Component
public class ReceiptConverter implements Converter<String, ReceiptDTO> {

    @Override
    public ReceiptDTO convert(String source) {
        return new ReceiptDTO(Long.parseLong(source));
    }
}