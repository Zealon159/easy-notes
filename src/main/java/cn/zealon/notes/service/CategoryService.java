package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.Category;
import cn.zealon.notes.repository.CategoryRepository;
import cn.zealon.notes.repository.NotesRepository;
import cn.zealon.notes.security.jwt.JwtAuthService;
import cn.zealon.notes.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类
 * @author: zealon
 * @since: 2020/12/22
 */
@Slf4j
@Service
public class CategoryService {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private NotesRepository notesRepository;

    public Result create(Category category) {
        try {
            category.setUserId(jwtAuthService.getLoginUserBean().getUsername());
            Category insert = this.categoryRepository.insert(category);
            return ResultUtil.success(insert);
        } catch (Exception ex) {
            log.error("保存分类异常!", ex);
            return ResultUtil.fail();
        }
    }

    public Result update(Category category) {
        try {
            String nowDateString = DateUtil.getNowDateString();
            Update update = Update.update("update_time", nowDateString);
            if (StringUtils.isNotBlank(category.getTitle())) {
                update.set("title", category.getTitle());
            }
            if (category.getSort() != null) {
                update.set("sort", category.getSort());
            }
            this.categoryRepository.updateOne(category.getId(), update);
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("更新分类异常!", ex);
            return ResultUtil.fail();
        }
    }

    public Result remove(Category category){
        try {
            long notesCount = this.notesRepository.findCountByCategoryId(category.getId());
            if (notesCount > 0) {
                return ResultUtil.verificationFailed().buildMessage("分类下有"+notesCount+"个笔记，请移动笔记到其它分类下再删除！");
            }
            this.categoryRepository.remove(category.getId());
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("删除分类异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 获取用户全部分类
     * @return
     */
    public Result getAllCategoryList() {
        String userId = jwtAuthService.getLoginUserBean().getUsername();
        List<Category> categories = this.getCategoryListByParentId(userId);
        List<CategoryVO> categoryVOS = new ArrayList<>();
        for (Category category : categories) {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(category, vo);
            categoryVOS.add(vo);
        }
        return ResultUtil.success(categoryVOS);
    }

    /**
     * 获取级联分类名称
     * @param id
     * @return
     */
    public Result getCategoryDetails(String id) {
        CategoryVO vo = new CategoryVO();
        Category category = this.categoryRepository.findOne(id);
        BeanUtils.copyProperties(category, vo);
        return ResultUtil.success(vo);
    }

    private List<Category> getCategoryListByParentId(String userId) {
        Query query = Query.query(Criteria.where("user_id").is(userId));
        query.with(Sort.by("sort").ascending());
        return this.categoryRepository.find(query);
    }
}
