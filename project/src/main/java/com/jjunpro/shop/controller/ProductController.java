package com.jjunpro.shop.controller;

import static com.jjunpro.shop.util.ClassPathUtil.ADMINPRODUCT;

import com.jjunpro.shop.dto.ProductDTO;
import com.jjunpro.shop.model.FileStorage;
import com.jjunpro.shop.model.Product;
import com.jjunpro.shop.model.ShopGroup;
import com.jjunpro.shop.service.FileStorageServiceImpl;
import com.jjunpro.shop.service.ProductServiceImpl;
import com.jjunpro.shop.service.ShopGroupServiceImpl;
import com.jjunpro.shop.util.IpUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final IpUtil                 ipUtil;
    private final ProductServiceImpl     productService;
    private final ShopGroupServiceImpl   shopGroupService;
    private final FileStorageServiceImpl fileStorageService;

    @GetMapping("")
    public String index(
            Model model
    ) {
        List<Product> productList = productService.findAll();
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
            Product product = productService.findById(id);
            model.addAttribute("productDTO", product);

            /* 업로드된 file 정보를 불러옵니다. */
            if (product.getFileStorageIds() != null) {
                List<FileStorage> dbFile = new ArrayList<>();

                String[] fileStorageArr = product.getFileStorageIds().split(",");

                for (String fileStorage : fileStorageArr) {
                    Optional<FileStorage> serviceById = fileStorageService
                            .findById(Long.parseLong(fileStorage.trim()));

                    serviceById.ifPresent(dbFile::add);
                }

                model.addAttribute("dbFile", dbFile);
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
    ) {
        if (bindingResult.hasErrors()) {
            this.getShopGroupList(model);
            model.addAttribute("productDTO", productDTO);

            return ADMINPRODUCT.concat("/setProductForm");
        }

        StringBuilder uploadFile = new StringBuilder();

        /**
         * - file remove
         */
        if (!productDTO.getDeleteFileStorageIds().isEmpty()) {
            String[] dbFileArr     = productDTO.getFileStorageIds().split(",");
            String[] deleteFileArr = productDTO.getDeleteFileStorageIds().split(",");

            /*
             * 삭제하려는 파일 id 값은 저장하면 안되므로
             * { uploadFile } 변수에 담아서 전송할 수 없도록 합니다.
             *
             * 업로드된 파일 정보와 삭제하려는 파일을 비교합니다.
             * 삭제하려는 파일정보가 업로드된 파일정보와 일치하지 않으면
             * 서버로 전송 저장되는 { uploadFile } 변수에 담아집니다.
             *
             * ex) 전송하는 File : 1, 2, 3, 4, 5
             *     삭제하는 File : 1, 7, 3, 6, 8
             *
             * uploadFile = { 1, 3 } , 이외 값들은 삭제됩니다.
             *
             * 2중 for문에서 삭제되는 값 확인되면 break 문을 사용하여 불필요한 탐색을 안하도록 중지합니다.
             */
            for (String dbFile : dbFileArr) {
                boolean equalsCheck = true;
                for (String deleteFile : deleteFileArr) {
                    if (dbFile.trim().equals(deleteFile.trim())) {
                        equalsCheck = false;
                        break;
                    }
                }

                if (equalsCheck) {
                    uploadFile.append(dbFile.trim()).append(",");
                }
            }

            if (uploadFile.length() == 0) {
                /* 저장되는 파일이 0 개 인경우 Null */
                productDTO.setFileStorageIds(null);
            } else {
                /*
                 * uploadFile 변수 끝자리에 오는 문자 ',' 콤마를 삭제합니다.
                 * ex) { 1, 2, 3, } => { 1, 2, 3 }
                 */
                productDTO.setFileStorageIds(uploadFile.substring(0, uploadFile.length() - 1));
            }

            this.fileStorageService.delete(deleteFileArr);
        }

        /**
         * - file upload
         *
         * 문자열로 저장하기전에 배열에 포함되는 특수문자 ([, ]) 가로를 삭제 후 저장해 줍니다.
         * 다른곳에서 불러와 문자열을 배열로 만들 때 따로 특수만자 제거 작업이 필요없도록 미리 설정하는 것입니다.
         */
        /* 업로드 파일이 하나 이상 존재하는 경우 */
        if (!productDTO.getFileStorage()[0].isEmpty()) {
            /*
             * 기존에 저장된 파일 id 값이 존재하고
             * uploadFile.length() 값이 0 인경우 => file remove 과정을 거치지 않은 경우
             *
             * 기존의 저장된 파일 id 값과 새로 저장하려는 파일 id 값을 같이 { uploadFile } 저장하여 전송합니다.
             * ex) 기존 저장된 파일 id { 1, 2, 3 }
             *     새로 저장되는 파일 Id { 5, 6 }
             *     uploadFile => { 1, 2, 3, 5, 6 }
             */
            if (!productDTO.getFileStorageIds().isEmpty() && uploadFile.length() == 0) {
                uploadFile.append(productDTO.getFileStorageIds()).append(",");
            }

            List<Long> longs = fileStorageService.uploadMultipleFiles(productDTO.getFileStorage());

            String replaceAll = longs.toString().replaceAll("[\\[\\]]", "");
            uploadFile.append(replaceAll);

            productDTO.setFileStorageIds(uploadFile.toString());
        }

        productDTO.setIp(ipUtil.getUserIp(request));
        productService.set(productDTO.toEntity());

        return "redirect:/product";
    }

    /* RedirectAttributes 사용하여 그룹 index 페이지에 상태 메세지 Attributes 전달합니다.  */
    @PostMapping("/delete")
    public String delete(Long id, RedirectAttributes model) {
        model.addFlashAttribute("message", productService.delete(id));

        return "redirect:/product";
    }

    /* 분류 리스트를 불러옵니다. */
    private void getShopGroupList(Model model) {
        List<ShopGroup> shopGroupList = shopGroupService.findByIsNullParentShopGroupId();
        model.addAttribute("shopGroupList", shopGroupList);
    }
}
