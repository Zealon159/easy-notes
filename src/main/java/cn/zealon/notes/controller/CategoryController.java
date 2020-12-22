package cn.zealon.notes.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类
 * @author: zealon
 * @since: 2020/12/22
 */
@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("list")
    public Result getCategoryListByParentId(String userId) {
        return this.categoryService.getAllCategoryList(userId);
    }
}
