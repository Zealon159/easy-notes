package cn.zealon.notes.service;

import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.Category;
import cn.zealon.notes.domain.Notes;
import cn.zealon.notes.repository.CategoryRepository;
import cn.zealon.notes.repository.NotesRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 给用户默认数据
 * @author: zealon
 * @since: 2020/12/30
 */
@Service
public class InitNotesDataService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private NotesRepository notesRepository;

    public void initData(String userId) {
        String nowDateString = DateUtil.getNowDateString();
        String title = DateTime.now().getYear() + "年度笔记";
        Category category = new Category();
        category.setTitle(title);
        category.setUserId(userId);
        category.setSort(1);
        category.setUpdateTime(nowDateString);
        category.setCreateTime(nowDateString);
        Category insert = this.categoryRepository.insert(category);

        category = new Category();
        category.setTitle(DateTime.now().toString("随时记"));
        category.setSort(2);
        category.setUserId(userId);
        category.setUpdateTime(nowDateString);
        category.setCreateTime(nowDateString);
        Category insert2 = this.categoryRepository.insert(category);

        category = new Category();
        category.setTitle(DateTime.now().toString("美食家"));
        category.setSort(3);
        category.setUserId(userId);
        category.setUpdateTime(nowDateString);
        category.setCreateTime(nowDateString);
        this.categoryRepository.insert(category);

        category = new Category();
        category.setTitle(DateTime.now().toString("旅行计划"));
        category.setSort(4);
        category.setUserId(userId);
        category.setUpdateTime(nowDateString);
        category.setCreateTime(nowDateString);
        Category insert4 = this.categoryRepository.insert(category);

        Notes notes = new Notes();
        List<String> tags = new ArrayList<>();
        tags.add("vue");
        tags.add("antd");
        tags.add("markdown");
        notes.setUserId(userId);
        notes.setCategoryId(insert.getId());
        notes.setTitle("Hello World");
        notes.setContent(":star: 开始记录吧");
        notes.setType("markdown");
        notes.setTags(tags);
        notes.setUpdateTime(nowDateString);
        notes.setCreateTime(nowDateString);
        this.notesRepository.insert(notes);

        Notes notes2 = new Notes();
        tags.clear();
        tags.add("OAuth2");
        tags.add("JWT");
        tags.add("Spring");
        tags.add("MongoDB");
        notes2.setUserId(userId);
        notes2.setCategoryId(insert2.getId());
        notes2.setTitle("欢迎使用 ^_^");
        notes2.setContent("随便写点什么吧");
        notes2.setType("rich_text");
        notes2.setTags(tags);
        notes2.setUpdateTime(nowDateString);
        notes2.setCreateTime(nowDateString);
        this.notesRepository.insert(notes2);

        notes = new Notes();
        tags = new ArrayList<>();
        tags.add("古城");
        tags.add("高山湖畔");
        tags.add("雪山");
        notes.setUserId(userId);
        notes.setCategoryId(insert4.getId());
        notes.setTitle("云南丽江");
        notes.setContent("# Day 1\n" +
                "丽江古城 —— 木府 —— 四方街\n" +
                "\n" +
                "# Day 2\n" +
                "丽江古城 —— 虎跳峡 —— 香格里拉\n" +
                "\n" +
                "# Day 3\n" +
                "香格里拉 —— 梅里雪山\n" +
                "\n" +
                "# Day 4\n" +
                "丽江古城 —— 泸沽湖");
        notes.setType("markdown");
        notes.setTags(tags);
        notes.setUpdateTime(nowDateString);
        notes.setCreateTime(nowDateString);
        this.notesRepository.insert(notes);
    }
}
