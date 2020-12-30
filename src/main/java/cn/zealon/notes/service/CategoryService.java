package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.Category;
import cn.zealon.notes.repository.CategoryRepository;
import cn.zealon.notes.security.jwt.JwtAuthService;
import cn.zealon.notes.vo.CategoryCascadeVO;
import cn.zealon.notes.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
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

    public Result create(Category category) {
        try {
            int level = 1;
            if (StringUtils.isNotBlank(category.getParentId())) {
                level = 2;
            }
            category.setLevel(level);
            String nowDateString = DateUtil.getNowDateString();
            category.setCreateTime(nowDateString);
            category.setUpdateTime(nowDateString);
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
            // 业务校验
            if (StringUtils.isNotBlank(category.getParentId())) {
                Category parentCategory = this.categoryRepository.findOne(category.getParentId());
                if (parentCategory != null && parentCategory.getLevel() == 2) {
                    return ResultUtil.verificationFailed().buildMessage("分类深度最多是两层哦！");
                }

                if (this.categoryRepository.getSubCategoryCountById(category.getId()) > 0) {
                    return ResultUtil.verificationFailed().buildMessage("当前子分类有数据哦，请先移动子分类到其它分类下再调整父分类！");
                }
            }

            String nowDateString = DateUtil.getNowDateString();
            Update update = Update.update("update_time", nowDateString);
            if (StringUtils.isNotBlank(category.getTitle())) {
                update.set("title", category.getTitle());
            }
            if (category.getSort() != null) {
                update.set("sort", category.getSort());
            }
            int level = 1;
            if (StringUtils.isNotBlank(category.getParentId())) {
                level = 2;
            }
            update.set("level", level);
            update.set("parent_id", category.getParentId());
            this.categoryRepository.updateOne(category.getId(), update);
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("更新分类异常!", ex);
            return ResultUtil.fail();
        }
    }

    public Result remove(Category category){
        try {
            this.categoryRepository.remove(category.getId());
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("删除分类异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 获取用户全部分类(深度2)
     * @return
     */
    public Result getAllCategoryList() {
        String parentId = "";
        String userId = jwtAuthService.getLoginUserBean().getUsername();
        List<Category> categories = this.getCategoryListByParentId(userId, parentId);
        List<CategoryVO> categoryVOS = new ArrayList<>();
        for (Category category : categories) {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(category, vo);
            List<Category> subCategories = this.getCategoryListByParentId(userId, category.getId());
            List<CategoryVO> subCategoryVOS = new ArrayList<>();
            for (Category sub : subCategories){
                CategoryVO subVo = new CategoryVO();
                BeanUtils.copyProperties(sub, subVo);
                subVo.setCategorys(new ArrayList<>());
                subCategoryVOS.add(subVo);
            }
            vo.setCategorys(subCategoryVOS);
            categoryVOS.add(vo);
        }
        return ResultUtil.success(categoryVOS);
    }

    /**
     * 获取级联分类名称
     * @param id
     * @return
     */
    public Result getCascadeCategoryNames(String id) {
        CategoryCascadeVO vo = new CategoryCascadeVO();
        Category category = this.categoryRepository.findOne(id);
        if (category != null) {
            Category category2 = this.categoryRepository.findOne(category.getParentId());
            if (category2 != null) {
                vo.setParentName(category2.getTitle());
            }
            vo.setName(category.getTitle());
        }
        return ResultUtil.success(vo);
    }

    private List<Category> getCategoryListByParentId(String userId, String parentId) {
        Query query = Query.query(Criteria.where("user_id").is(userId));
        if (StringUtils.isBlank(parentId)) {
            parentId = "";
            query.addCriteria(Criteria.where("parent_id").is(parentId));
        } else {
            query.addCriteria(Criteria.where("parent_id").is(new ObjectId(parentId)));
        }
        query.with(Sort.by("sort").ascending());
        return this.categoryRepository.find(query);
    }
}
