package cn.zealon.notes.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.List;

/**
 * 标签
 * @author: zealon
 * @since: 2021/1/14
 */
@Data
@Document(collection = "tags")
public class Tag {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("name")
    private String name;

    @Field("notes_ids")
    private List<String> notesIds;

    @Field("create_time")
    private String createTime;

    @Field("update_time")
    private String updateTime;
}