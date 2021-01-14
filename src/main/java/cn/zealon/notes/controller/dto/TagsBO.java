package cn.zealon.notes.controller.dto;

import lombok.Data;
import java.util.List;

/**
 * 标签
 * @author: zealon
 * @since: 2021/1/14
 */
@Data
public class TagsBO {
    private String notesId;
    private Integer type;
    private String tagName;
    private List<String> tags;
}
