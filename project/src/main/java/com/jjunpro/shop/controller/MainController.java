package com.jjunpro.shop.controller;

import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ProductServiceImpl productService;
    private final IpUtil             ipUtil;

    @GetMapping("/")
    public String main(Model model, HttpServletRequest request) {
        List<Product> productList = this.productService.findAll(false);
        model.addAttribute("productList", productList);

        /* 사이트에 접근하는유저 확인 */
        SimpleDateFormat format      = new SimpleDateFormat("yyyy년 MM월dd일 HH시mm분ss초");
        Calendar         time        = Calendar.getInstance();
        String           format_time = format.format(time.getTime());
        String           userIp      = ipUtil.getUserIp(request);

        log.info("user_ip : " + userIp);
        log.info("format_time : " + format_time);

        return "main/main";
    }
}
