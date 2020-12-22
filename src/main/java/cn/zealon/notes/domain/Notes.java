package cn.zealon.notes.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 笔记
 * @author: zealon
 * @since: 2020/12/21
 */
@Data
@Document(collection = "notes")
public class Notes {

    @Id
    private Integer id;

    @Field("type")
    private Integer type;

    @Field("subject")
    private String subject;

    @Field("content")
    private String content;

    @Field("update_time")
    private Date updateTime;

}
