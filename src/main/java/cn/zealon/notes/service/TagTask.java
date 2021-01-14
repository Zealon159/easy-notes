package cn.zealon.notes.service;

import cn.zealon.notes.controller.dto.TagsBO;
import cn.zealon.notes.domain.Tag;

/**
 * 标签处理任务
 * @author: zealon
 * @since: 2021/1/14
 */
public class TagTask implements Runnable {

    private TagsBO tagsBO;
    private TagService tagService;
    private String userId;

    @Override
    public void run() {
        Tag tag = new Tag();
        tag.setUserId(userId);
        tag.setName(tagsBO.getTagName());
        if (tagsBO.getType() == 1) {
            // 增加保存标签
            tagService.saveTag(tag, tagsBO.getNotesId());
        } else {
            // 减少删除标签
            tagService.reduceTag(tag, tagsBO.getNotesId());
        }
    }

    public TagTask(){}

    public TagTask(TagsBO tagsBO, TagService tagService, String userId) {
        this.tagsBO = tagsBO;
        this.tagService = tagService;
        this.userId = userId;
    }
}
