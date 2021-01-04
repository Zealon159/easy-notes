package cn.zealon.notes.controller;

import cn.zealon.notes.common.base.BaseController;
import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.domain.Category;
import cn.zealon.notes.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result create(@RequestBody Category category){
        return this.categoryService.create(category);
    }

    @PostMapping("update")
    public Result update(@RequestBody Category category){
        return this.categoryService.update(category);
    }

    @PostMapping("delete")
    public Result delete(@RequestBody Category category){
        return this.categoryService.remove(category);
    }

    @GetMapping("list")
    public Result getCategoryListByParentId() {
        return this.categoryService.getAllCategoryList();
    }

    @GetMapping("details")
    public Result getCategoryDetails(String id) {
        return this.categoryService.getCategoryDetails(id);
    }
}
