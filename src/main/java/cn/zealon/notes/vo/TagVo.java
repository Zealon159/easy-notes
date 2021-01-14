package cn.zealon.notes.vo;

import lombok.Data;

/**
 * 标签
 * @author: zealon
 * @since: 2021/1/14
 */
@Data
public class TagVo {
    private String id;
    private String name;
    private Integer notesCount;
}
