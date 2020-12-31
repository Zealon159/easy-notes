package cn.zealon.notes.service;

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
        String title = DateTime.now().getYear() + "年";
        Category category = new Category();
        category.setTitle(title);
        category.setLevel(1);
        category.setParentId("");
        category.setUserId(userId);
        Category insert = this.categoryRepository.insert(category);

        Category sub1 = new Category();
        sub1.setTitle(DateTime.now().toString("yyyy年MM月"));
        sub1.setLevel(2);
        sub1.setParentId(insert.getId());
        sub1.setSort(2);
        sub1.setUserId(userId);
        Category insert1 = this.categoryRepository.insert(sub1);

        Category sub2 = new Category();
        sub2.setTitle(DateTime.now().plusMonths(-1).toString("yyyy年MM月"));
        sub2.setLevel(2);
        sub2.setParentId(insert.getId());
        sub2.setSort(1);
        sub2.setUserId(userId);
        this.categoryRepository.insert(sub2);

        Notes notes = new Notes();
        List<String> tags = new ArrayList<>();
        tags.add("vue");
        tags.add("antd");
        tags.add("markdown");
        notes.setUserId(userId);
        notes.setCategoryId(insert.getId());
        notes.setCategorySubId(insert1.getId());
        notes.setTitle("Hello World");
        notes.setContent(":star: 开始记录吧");
        notes.setType("markdown");
        notes.setTags(tags);
        this.notesRepository.insert(notes);

        Notes notes2 = new Notes();
        tags.clear();
        tags.add("OAuth2");
        tags.add("JWT");
        tags.add("Spring");
        tags.add("MongoDB");
        notes2.setUserId(userId);
        notes2.setCategoryId(insert.getId());
        notes2.setCategorySubId(insert1.getId());
        notes2.setTitle("欢迎使用 ^_^");
        notes2.setContent("随便写点什么吧");
        notes2.setType("rich_text");
        notes2.setTags(tags);
        this.notesRepository.insert(notes2);
    }
}
