package cn.zealon.notes.controller.dto;

import lombok.Data;

/**
 * 笔记查询
 * @author: zealon
 * @since: 2020/12/24
 */
@Data
public class NotesQuery {
    private Integer star;
    private Integer delete;
    private String categoryId;
    /** 0:查全部  1:分类查询 */
    private Integer level;
    private Integer limit;
    /** 排序字段 */
    private String sortBy;
    /** 排序方位 0:降序 1:升序 */
    private Integer direction;
}
