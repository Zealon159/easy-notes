package cn.zealon.notes.vo;

import lombok.Data;

/**
 * 笔记列表VO
 * @author: zealon
 * @since: 2020/12/24
 */
@Data
public class NotesItemVO {
    private String id;
    private String type;
    private String title;
    private Integer star;
    private String createTime;
    private String updateTime;
}
