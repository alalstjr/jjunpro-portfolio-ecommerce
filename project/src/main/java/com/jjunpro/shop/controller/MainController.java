package com.jjunpro.shop.controller;

import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.security.context.AccountContext;
import com.jjunpro.shop.service.AccountServiceImpl;
import com.jjunpro.shop.service.ProductAccessServiceImpl;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.util.AccountUtil;
import com.jjunpro.shop.util.IpUtil;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductServiceImpl       productService;
    private final ProductAccessServiceImpl productAccessService;
    private final AccountServiceImpl       accountService;
    private final IpUtil                   ipUtil;
    private final AccountUtil              accountUtil;

    @GetMapping("/")
    public String main(
            Model model,
            HttpServletRequest request,
            Principal principal
    ) {
        List<Product> productList = this.productService.findAll(false);
        model.addAttribute("productList", productList);

        /* Account 나이를 파악하여 추천상품을 나열합니다. */
        if (principal != null) {
            AccountContext dbAccount = (AccountContext) this.accountService
                    .loadUserByUsername(principal.getName());
            /* Account 정보에 등록된 나이 or 생일 정보가 있는경우에만 탐색 */
            if (dbAccount.getAccount().getAgeRange() != null
                    || dbAccount.getAccount().getBirthday() != null) {
                byte ageConverter = accountUtil.ageConverter(
                        dbAccount.getAccount().getAgeRange(),
                        dbAccount.getAccount().getBirthday()
                );

                List<Product> ageAccessByProductList = productAccessService
                        .getAgeAccessByProductUser(ageConverter);
                model.addAttribute("ageAccessByProductList", ageAccessByProductList);
            }
        } else {
            model.addAttribute("ageAccessByProductList", productList);
        }

        /* 사이트에 접근하는유저 확인 */
        DateTime dt     = new DateTime();
        String   now    = dt.toString("yyyy-MM-dd HH:mm:ss");
        String   userIp = ipUtil.getUserIp(request);

        logger.info("UserPoint ======== {");
        logger.info("접근경로 URL : " + request.getHeader("Referer"));
        logger.info("접속한 ip : " + userIp);
        logger.info("시간 : " + now);
        logger.info("}");

        return "main/main";
    }
}
