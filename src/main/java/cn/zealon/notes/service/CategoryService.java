package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.Category;
import cn.zealon.notes.vo.CategoryCascadeVO;
import cn.zealon.notes.vo.CategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private MongoTemplate mongoTemplate;

    public Result save(Category category) {
        try {
            int level = 1;
            if (StringUtils.isNotBlank(category.getParentId())) {
                level = 2;
            }
            category.setLevel(level);
            String nowDateString = DateUtil.getNowDateString();
            category.setUpdateTime(nowDateString);
            this.mongoTemplate.save(category);
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("保存分类异常!", ex);
            return ResultUtil.fail();
        }
    }

    public Result remove(Category category){
        try {
            this.mongoTemplate.remove(category);
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("删除分类异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 获取用户全部分类(深度2)
     * @param userId
     * @return
     */
    public Result getAllCategoryList(String userId) {
        String parentId = "";
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
        Query query = Query.query(Criteria.where("_id").is(id));
        Category category = this.mongoTemplate.findOne(query, Category.class);
        if (category != null) {
            Query query2 = Query.query(Criteria.where("_id").is(category.getParentId()));
            Category category2 = this.mongoTemplate.findOne(query2, Category.class);
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
        return this.mongoTemplate.find(query, Category.class);
    }
}
