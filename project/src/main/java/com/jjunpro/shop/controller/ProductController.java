package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINPRODUCT;
import static com.jjunpro.shop.util.ClassPathUtil.SHOP;

import com.jjunpro.shop.dto.ProductDTO;
import com.jjunpro.shop.dto.ProductSetDTO;
import com.jjunpro.shop.enums.DomainType;
import com.jjunpro.shop.exception.DataNullException;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ShopGroup;
import com.jjunpro.shop.service.FileStorageServiceImpl;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.service.ShopGroupServiceImpl;
import com.jjunpro.shop.util.FileUtil;
import com.jjunpro.shop.util.IpUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
@SessionAttributes("productSet")
public class ProductController {

    private final IpUtil                 ipUtil;
    private final ProductServiceImpl     productService;
    private final ShopGroupServiceImpl   shopGroupService;
    private final FileStorageServiceImpl fileStorageService;
    private final FileUtil               fileUtil;

    @GetMapping("")
    public String index(
            Model model
    ) {
        List<Product> productList = productService.findAll(true);
        model.addAttribute("productList", productList);

        return ADMINPRODUCT.concat("/index");
    }

    @GetMapping("/set")
    public String initSet(
            Model model,
            @RequestParam(required = false) Long id,
            RedirectAttributes redirectAttributes
    ) {
        /* 분류가 하나이상 존재하는지 확인합니다. */
        if (shopGroupService.allCount() == 0) {
            redirectAttributes.addFlashAttribute("message", "분류가 하나이상 존재해야 상품등록이 가능합니다.");

            return "redirect:/shopgroup/set";
        }

        ProductDTO productDTO = new ProductDTO();

        /* 수정 id */
        if (id != null) {
            Optional<Product> product = productService.findById(id);

            if (product.isPresent()) {
                model.addAttribute("productDTO", product.get());

                /* 업로드된 file 정보를 불러옵니다. */
                if (product.get().getFileStorageIds() != null) {
                    List<FileStorage> dbFile = new ArrayList<>();

                    String[] fileStorageArr = product.get().getFileStorageIds().split(",");

                    for (String fileStorage : fileStorageArr) {
                        Optional<FileStorage> serviceById = fileStorageService
                                .findById(Long.parseLong(fileStorage.trim()));

                        serviceById.ifPresent(dbFile::add);
                    }

                    model.addAttribute("dbFile", dbFile);
                }
            } else {
                throw new DataNullException("상품이 존재하지 않습니다.");
            }
        } else {
            model.addAttribute("productDTO", productDTO);
        }

        this.getShopGroupList(model);

        return ADMINPRODUCT.concat("/setProductForm");
    }

    @PostMapping("/set")
    public String set(
            HttpServletRequest request,
            @Valid ProductDTO productDTO,
            BindingResult bindingResult,
            Model model
    ) throws NoSuchFieldException, IllegalAccessException {
        if (bindingResult.hasErrors()) {
            this.getShopGroupList(model);
            model.addAttribute("productDTO", productDTO);

            return ADMINPRODUCT.concat("/setProductForm");
        }

        /* File Upload / delete */
        this.fileUtil.setFileHandler(productDTO, DomainType.PRODUCT);

        productDTO.setIp(ipUtil.getUserIp(request));
        productService.set(productDTO.toEntity());

        return "redirect:/product";
    }

    /* RedirectAttributes 사용하여 그룹 index 페이지에 상태 메세지 Attributes 전달합니다.  */
    @PostMapping("/delete")
    public String delete(Long id, RedirectAttributes model) {
        /* DB 조회 후 삭제하려는 DATA 에 파일정보가 있으면 같이 삭제 */
        Optional<Product> dbProduct = productService.findById(id);

        if (dbProduct.isPresent()) {
            if (dbProduct.get().getFileStorageIds() != null) {
                String[] fileStorageArr = dbProduct.get().getFileStorageIds().split(",");
                fileStorageService.delete(fileStorageArr, DomainType.PRODUCT);
            }
        } else {
            throw new DataNullException("상품이 존재하지 않습니다.");
        }

        model.addFlashAttribute("message", productService.delete(id));

        return "redirect:/product";
    }

    @GetMapping("/view")
    public String view(
            @RequestParam Long id,
            Model model
    ) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
        }

        model.addAttribute("productSet", new ProductSetDTO());

        return SHOP.concat("/productView");
    }

    @PostMapping("/view")
    public String viewSet(
            @ModelAttribute ProductSetDTO productSet,
            Model model
    ) {
        /* Session 저장소에 상품 id, 수량 등등 기타정보를 담아서 주문서로 넘깁니다. */
        model.addAttribute("productSet", productSet);

        return "redirect:/product/order";
    }

    @GetMapping("/order")
    public String order(
            Model model
    ) {
        ProductSetDTO productSet = (ProductSetDTO) model.getAttribute("productSet");

        return SHOP.concat("/productOrder");
    }


    /* 분류 리스트를 불러옵니다. */
    private void getShopGroupList(Model model) {
        List<ShopGroup> shopGroupList = this.shopGroupService.findByIsNullParentShopGroupId();
        model.addAttribute("shopGroupList", shopGroupList);
    }
}
