package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.repository.CategoryRepository;
import cn.zealon.notes.repository.NotesRepository;
import cn.zealon.notes.repository.TagRepository;
import cn.zealon.notes.security.jwt.JwtAuthService;
import cn.zealon.notes.vo.DashboardVO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 仪表盘
 * @author: zealon
 * @since: 2021/1/7
 */
@Service
public class DashboardService {

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private TagRepository tagRepository;

    /**
     * 获取仪表盘数据
     * @return
     */
    public Result getDashboardSummary(){
        DashboardVO vo = new DashboardVO();
        String userId = jwtAuthService.getLoginUserBean().getUsername();
        vo.setCategoryCount(categoryRepository.findCountByUserId(userId));
        vo.setNotesCount(notesRepository.findCountByUserId(userId));
        vo.setClassicQuotations(this.getClassicQuotations());
        vo.setTagCount(tagRepository.findCountByUserId(userId));
        this.getWelcome(vo);
        return ResultUtil.success(vo);
    }

    /**
     * 经典语录
     */
    private List<String> getClassicQuotations(){
        String[] arr = "态度决定一切,仰望星空，脚踏实地,道路艰难，唯勇者行,聪明是一种天赋，而善良是一种选择,最重要的是把要事当做要事,陷入爱河，人人都会变成诗人,青春不是年华，而是心境,生活并不公平，接受它吧,我是一个与众不同的存在,人不是为失败而生,爱所有人，信部分人，不伤害任何人,有信仰，有热情的人生值得一过"
                .split(",");
        List<String> list = new ArrayList<>(arr.length);
        for(String s : arr){
            list.add(s);
        }
        return list;
    }

    /** 欢迎语 */
    private String getWelcome(DashboardVO vo){
        int hour = DateTime.now().getHourOfDay();
        String greet = "";
        String welcome = "，深夜了要抓紧休息啦";
        if(hour > 5 && hour < 10){
            greet = "早安，";
            welcome =  "，今天也要元气满满哦";
        } else if (hour >= 10 && hour < 14){
            greet = "中午好，";
            welcome =  "，祝你开心每一天";
        } else if (hour >= 14 && hour < 18){
            greet = "下午好，";
            welcome =  "，晚上要吃顿好的呀";
        } else if (hour >= 18 && hour < 22){
            greet = "晚上好，";
            welcome =  "，今天也要早早休息哦";
        }
        vo.setGreet(greet);
        vo.setWelcome(welcome);
        return welcome;
    }
}
