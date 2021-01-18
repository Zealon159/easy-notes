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
        Category category = new Category();
        category.setTitle("时光杂货铺");
        category.setUserId(userId);
        category.setSort(1);
        category.setUpdateTime(nowDateString);
        category.setCreateTime(nowDateString);
        Category insert = this.categoryRepository.insert(category);

        category = new Category();
        category.setTitle("随时记");
        category.setSort(2);
        category.setUserId(userId);
        category.setUpdateTime(nowDateString);
        category.setCreateTime(nowDateString);
        Category insert2 = this.categoryRepository.insert(category);

        category = new Category();
        category.setTitle("美食家");
        category.setSort(3);
        category.setUserId(userId);
        category.setUpdateTime(nowDateString);
        category.setCreateTime(nowDateString);
        this.categoryRepository.insert(category);

        category = new Category();
        category.setTitle("My Work");
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
        notes.setTitle("欢迎使用 ^_^");
        notes.setStar(1);
        notes.setContent("# 欢迎使用 :clap:\n" +
                "- 支持自定义分类\n" +
                "- 支持Markdown、富文本编辑器\n" +
                "- 也支持社交账户登录哦\n" +
                "- 支持自定义标签(标签统计开发中 :smirk_cat:)\n" +
                "- 当然也会持续更新功能哦...\n" +
                "\n" +
                "> 相关连接：\n" +
                "开源地址: [Github](https://github.com/Zealon159/easy-notes)\n");
        notes.setType("markdown");
        notes.setTags(tags);
        notes.setUpdateTime(nowDateString);
        notes.setCreateTime(nowDateString);
        this.notesRepository.insert(notes);

        Notes notes2 = new Notes();
        notes2.setUserId(userId);
        notes2.setCategoryId(insert.getId());
        notes2.setTitle("时空旅行者宴会");
        notes2.setContent("<p>&nbsp; &nbsp; 2009年6月28日，英国著名物理学家霍金举行了一次时间旅行者宴会，欢迎未来的人们回到现在，参加这个宴会。房间布置了气球、香槟和美食，挂著大大的标语，写着\"欢迎时间旅行者\"。<br></p><p><img src=\"https://www.wangbase.com/blogimg/asset/201908/bg2019081104.jpg\"><br></p><p>&nbsp; &nbsp; 霍金在宴会举办前没有向任何人发出邀请。宴会结束后，他才发出请帖，邀请有\"穿越\"能力的人士赴宴。<br></p><p><img src=\"https://www.wangbase.com/blogimg/asset/201908/bg2019081106.jpg\"><br></p><p>&nbsp; &nbsp; 请帖上写著：\"诚挚邀请你参加时间旅行者的宴会。宴会由史蒂芬·霍金教授举办。\"请帖不但写明宴会的举办地点为英国剑桥大学冈维尔与凯斯学院，还贴心地标明了经纬度。</p><p>&nbsp; &nbsp; 霍金认为，如果有未来人看到这份请帖，并且能\"穿越\"回到过去，那么他在那次宴会上就会见到货真价实的\"时间旅行者\"。</p><p><img src=\"https://www.wangbase.com/blogimg/asset/201908/bg2019081105.jpg\"><br></p><p>&nbsp; &nbsp; 但是，完全没有人赴会。霍金说：\"我坐了很长时间，但没有人来。我用实验证明，时间旅行不可能。\"</p><p>&nbsp; &nbsp; 1905年，爱因斯坦在相对论中断定，当接近光速的运动时，时间会很慢或静止，也就是说，当人乘坐接近光速的飞船去旅行，在旅行的过程中时间就会变慢，因此，当他再回到地球的时候就可能已经过了一个世纪。对他来 说，只要花很少的时间就能进入未来世界。但是，爱因斯坦指出：光速无法超越，时间不可能倒流。</p><p>&nbsp; &nbsp; 不过，霍金说：\"从爱因斯坦的相对论来看，弯曲时空、回到过去的可能性的确存在。但是，让时空弯曲，可能引发能量摧毁（时空旅行所用的）飞船、甚至时空。\"　</p><p> </p>");
        notes2.setType("rich_text");
        notes2.setTags(tags);
        notes2.setUpdateTime(nowDateString);
        notes2.setCreateTime(nowDateString);
        this.notesRepository.insert(notes2);

        notes = new Notes();
        notes.setUserId(userId);
        notes.setCategoryId(insert4.getId());
        notes.setTitle("广告联盟数据模型梳理");
        notes.setContent("# 数据模型\n" +
                "\n" +
                "| 数据         | 百青藤             | 穿山甲           | VIVO             |\n" +
                "| ------------ | ------------------ | ---------------- | ---------------- |\n" +
                "| 广告联盟     | :ok:               | :ok:             | :ok:             |\n" +
                "| 日期         | :ok:               | :ok:             | :ok:         |\n" +
                "| 所属应用     | :ok:         | :ok:     | :ok:             |\n" +
                "| 操作系统     | :ok:     | :ok:         | :x:              |\n" +
                "| 广告样式     | :star:  | :ok: | :x:              |\n" +
                "| 代码位ID     | :ok:   | :ok:    | :ok:   |\n" +
                "| 代码位名称   | :ok: | :x:              | :ok: |\n" +
                "| 广告请求     | :x:                | :ok:     | :x:              |\n" +
                "| 广告返回     | :x:                | :ok:    | :x:              |\n" +
                "| 代码位展现量 | :ok:           | :ok:     | :ok:         |\n" +
                "| 预计收入     | :ok:         | :ok:   | :ok:       |\n" +
                "| 点击量       | :ok:          | :ok:   | :ok:        |\n" +
                "| eCPM         | :ok:          | :ok:       | :ok:         |\n" +
                "| 点击率       | :ok:            | :ok:   | :ok:   |\n" +
                "| CPC          | :ok:            | :x:              | :ok:          |");
        notes.setType("markdown");
        notes.setUpdateTime(nowDateString);
        notes.setCreateTime(nowDateString);
        this.notesRepository.insert(notes);

        notes = new Notes();
        notes.setUserId(userId);
        notes.setCategoryId(insert2.getId());
        notes.setTitle("诗歌收藏");
        notes.setContent("<p style=\"padding-left:2em;\"><br><font size=\"3\">洁白的仙鹤（仓央嘉措）</font></p><p style=\"padding-left:2em;\"><font size=\"3\" color=\"#4d80bf\">洁白的仙鹤啊，请借我一双翅膀，</font></p><p style=\"padding-left:2em;\"><font size=\"3\" color=\"#4d80bf\">我不会远走高飞，到理塘转转就回。</font></p>");
        notes.setStar(1);
        notes.setType("rich_text");
        notes.setTags(tags);
        notes.setUpdateTime(nowDateString);
        notes.setCreateTime(nowDateString);
        this.notesRepository.insert(notes);
    }
}
