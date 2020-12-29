package cn.zealon.notes.vo;

import lombok.Data;

/**
 * 级联分类名称
 * @author: zealon
 * @since: 2020/12/24
 */
@Data
public class CategoryCascadeVO {
    private String parentName;
    private String name;
}
