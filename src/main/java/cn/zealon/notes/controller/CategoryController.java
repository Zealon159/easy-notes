package cn.zealon.notes.controller;

import cn.zealon.notes.common.base.BaseController;
import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分类
 * @author: zealon
 * @since: 2020/12/22
 */
@RestController
@RequestMapping("category")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("create")
    public Result create(){
        return ResultUtil.success();
    }

    @GetMapping("list")
    public Result getCategoryListByParentId(String userId) {
        return this.categoryService.getAllCategoryList(userId);
    }

    @GetMapping("details")
    public Result getCascadeCategoryNames(String id) {
        return this.categoryService.getCascadeCategoryNames(id);
    }
}
