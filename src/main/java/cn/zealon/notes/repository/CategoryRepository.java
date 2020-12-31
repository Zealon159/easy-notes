package cn.zealon.notes.repository;

import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分类
 * @author: zealon
 * @since: 2020/12/22
 */
@Repository
public class CategoryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Category findOne(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return this.mongoTemplate.findOne(query, Category.class);
    }

    /**
     * 获取子分类数量
     * @param id
     * @return
     */
    public long getSubCategoryCountById(String id) {
        Query queryCount = Query.query(Criteria.where("_id").is(id));
        return this.mongoTemplate.count(queryCount, "category");
    }

    public Category insert(Category category){
        String nowDateString = DateUtil.getNowDateString();
        category.setCreateTime(nowDateString);
        category.setUpdateTime(nowDateString);
        return this.mongoTemplate.insert(category);
    }

    /**
     * 按ID更新
     * @param id
     * @param update
     */
    public void updateOne(String id, Update update) {
        Query query = Query.query(Criteria.where("_id").is(id));
        this.mongoTemplate.updateFirst(query, update, Category.class);
    }

    public void remove(String id){
        Query query = Query.query(Criteria.where("_id").is(id));
        this.mongoTemplate.remove(query, "category");
    }

    public List<Category> find(Query query){
        return this.mongoTemplate.find(query, Category.class);
    }
}
