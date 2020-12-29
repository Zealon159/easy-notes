package cn.zealon.notes.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

/**
 * 笔记
 * @author: zealon
 * @since: 2020/12/21
 */
@Data
@Document(collection = "notes")
public class Notes {

    @Id
    private String id;

    @Field("type")
    private String type;

    @Field("user_id")
    private String userId;

    @Field("title")
    private String title;

    @Field("content")
    private String content;

    @Field("star")
    private Integer star;

    @Field("delete")
    private Integer delete;

    @Field("tags")
    private List<String> tags;

    @Field("category_id")
    private String categoryId;

    @Field("category_sub_id")
    private String categorySubId;

    @Field("create_time")
    private String createTime;

    @Field("update_time")
    private String updateTime;

}
