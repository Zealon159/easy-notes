package cn.zealon.notes.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 分类
 * @author: zealon
 * @since: 2020/12/22
 */
@Data
@Document(collection = "category")
public class Category {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("title")
    private String title;

    @Field("parent_id")
    private String parentId;

    @Field("sort")
    private Integer sort;

    @Field("level")
    private Integer level;

    @Field("create_time")
    private String createTime;

    @Field("update_time")
    private String updateTime;
}
