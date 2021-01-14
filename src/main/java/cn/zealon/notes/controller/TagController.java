package cn.zealon.notes.controller;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签接口
 * @author: zealon
 * @since: 2021/1/14
 */
@RestController
@RequestMapping("tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 查询标签列表
     * @return
     */
    @GetMapping("list")
    public Result getTagList(){
        return this.tagService.getTagList();
    }
}
