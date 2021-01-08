package cn.zealon.notes.repository;

import cn.zealon.notes.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    public void updateOne(String userId, Update update){
        Query query = Query.query(Criteria.where("_id").is(userId));
        this.mongoTemplate.updateFirst(query, update, User.class);
        this.mongoTemplate.update(User.class);
    }

    public User findUserByUserId(String userId){
        Query query = Query.query(Criteria.where("_id").is(userId));
        return this.mongoTemplate.findOne(query, User.class);
    }

    /**
     * 按OAuth2查找用户
     * @param clientName
     * @param name
     * @return
     */
    public User findUserByOAuth2Client(String clientName, String name){
        Query query = Query.query(Criteria.where("auth2_clients.clientName").is(clientName));
        query.addCriteria(Criteria.where("auth2_clients.name").is(name));
        return this.mongoTemplate.findOne(query, User.class);
    }
}
