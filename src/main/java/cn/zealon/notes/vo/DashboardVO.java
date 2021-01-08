package cn.zealon.notes.vo;

import lombok.Data;

import java.util.List;

/**
 * 仪表盘
 * @author: zealon
 * @since: 2021/1/7
 */
@Data
public class DashboardVO {
    private Long notesCount;
    private Long categoryCount;
    private Long tagCount;
    private List<String> classicQuotations;
    private String greet;
    private String welcome;
}
