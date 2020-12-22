package cn.zealon.notes.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 分类
 * @author: zealon
 * @since: 2020/12/22
 */
@Repository
public class CategoryRepository {

    @Autowired
    private MongoTemplate mongoTemplate;
}
