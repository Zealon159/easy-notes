package cn.zealon.notes.repository;

import cn.zealon.notes.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * 用户
 * @author: zealon
 * @since: 2020/12/21
 */
@Repository
public class UserRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insertOne(User user) {
        this.mongoTemplate.save(user);
    }

    public void updateOne(User user){
        this.mongoTemplate.update(User.class);
    }

    public User getUserByUserId(String userId){
        Query query = Query.query(Criteria.where("_id").is(userId));
        return this.mongoTemplate.findOne(query, User.class);
    }
}
