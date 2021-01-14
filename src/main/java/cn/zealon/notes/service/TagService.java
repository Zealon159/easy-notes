package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.domain.Tag;
import cn.zealon.notes.repository.TagRepository;
import cn.zealon.notes.security.jwt.JwtAuthService;
import cn.zealon.notes.vo.TagVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 标签服务
 * @author: zealon
 * @since: 2021/1/14
 */
@Slf4j
@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private JwtAuthService jwtAuthService;

    /**
     * 存储标签
     * @param tag 标签对象
     */
    public void saveTag(Tag tag, String notesId) {
        try {
            if (StringUtils.isBlank(notesId)) {
                return;
            }

            Tag dbTag = this.tagRepository.findOneByName(tag.getUserId(), tag.getName());
            if (dbTag == null) {
                // 全新新增
                List<String> list = new ArrayList<>();
                list.add(notesId);
                tag.setNotesIds(list);
                this.tagRepository.insert(tag);
            } else {
                // 更新
                List<String> notesIds = dbTag.getNotesIds();
                boolean add = true;
                for (String id : notesIds){
                    if (id.equals(notesId)) {
                        add = false;
                    }
                }
                if (add) {
                    notesIds.add(notesId);
                    String nowDateString = DateUtil.getNowDateString();
                    Update update = Update.update("update_time", nowDateString);
                    update.set("notes_ids", notesIds);
                    this.tagRepository.update(dbTag.getId(), update);
                }
            }
        } catch (Exception ex) {
            log.error("存储标签异常!", ex);
        }
    }

    /**
     * 减少标签
     * @param tag
     */
    public void reduceTag(Tag tag, String notesId){
        try {
            Tag dbTag = this.tagRepository.findOneByName(tag.getUserId(), tag.getName());
            if (dbTag == null) {
                return;
            }

            if (dbTag.getNotesIds() != null
                    && dbTag.getNotesIds().size() > 0) {
                List<String> notesIds = dbTag.getNotesIds();
                for (int i = 0; i < notesIds.size(); i++) {
                    if (notesIds.get(i).equals(notesId)) {
                        notesIds.remove(i);
                        break;
                    }
                }
                if (notesIds.size() == 0) {
                    this.tagRepository.remove(dbTag.getId());
                } else {
                    String nowDateString = DateUtil.getNowDateString();
                    Update update = Update.update("update_time", nowDateString);
                    update.set("notes_ids", notesIds);
                    this.tagRepository.update(dbTag.getId(), update);
                }
            }
        } catch (Exception ex) {
            log.error("减少标签异常!", ex);
        }
    }

    /**
     * 查询标签列表
     * @return
     */
    public Result getTagList(){
        List<TagVo> vos = new ArrayList<>();
        String userId = jwtAuthService.getLoginUserBean().getUsername();
        Query query = Query.query(Criteria.where("user_id").is(userId));
        // 排序规则
        Sort sort = Sort.by("notes_ids").descending();
        query.with(sort);

        // 执行查询
        List<Tag> notesList = this.tagRepository.findList(query);
        for(Tag tag : notesList) {
            TagVo vo = new TagVo();
            BeanUtils.copyProperties(tag, vo);
            int count = 0;
            if (tag.getNotesIds() != null) {
                count = tag.getNotesIds().size();
            }
            vo.setNotesCount(count);
            vos.add(vo);
        }
        return ResultUtil.success(vos);
    }
}
