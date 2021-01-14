package cn.zealon.notes.repository;

import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 标签
 * @author: zealon
 * @since: 2021/1/14
 */
@Repository
public class TagRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Tag insert(Tag tag) {
        String nowDateString = DateUtil.getNowDateString();
        tag.setCreateTime(nowDateString);
        tag.setUpdateTime(nowDateString);
        return this.mongoTemplate.insert(tag);
    }

    public void update(String id, Update update){
        Query query = Query.query(Criteria.where("_id").is(id));
        this.mongoTemplate.updateFirst(query, update, Tag.class);
    }

    public void remove(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        this.mongoTemplate.remove(query, "tags");
    }

    public Tag findOne(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return this.mongoTemplate.findOne(query, Tag.class);
    }

    public Tag findOneByName(String userId, String name) {
        Query query = Query.query(Criteria.where("user_id").is(userId));
        query.addCriteria(Criteria.where("name").is(name));
        return this.mongoTemplate.findOne(query, Tag.class);
    }

    public long findCountByUserId(String userId) {
        Query queryCount = Query.query(Criteria.where("user_id").is(userId));
        return this.mongoTemplate.count(queryCount, "tags");
    }

    public List<Tag> findList(Query query){
        return this.mongoTemplate.find(query, Tag.class);
    }

}
