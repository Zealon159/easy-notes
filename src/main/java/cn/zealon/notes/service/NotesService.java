package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.controller.dto.NotesQuery;
import cn.zealon.notes.controller.dto.TagsBO;
import cn.zealon.notes.domain.Notes;
import cn.zealon.notes.repository.CategoryRepository;
import cn.zealon.notes.repository.NotesRepository;
import cn.zealon.notes.security.jwt.JwtAuthService;
import cn.zealon.notes.vo.NotesItemVO;
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
import java.util.concurrent.ExecutorService;

/**
 * 笔记服务
 * @author: zealon
 * @since: 2020/12/24
 */
@Slf4j
@Service
public class NotesService {

    @Autowired
    private NotesRepository notesRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtAuthService jwtAuthService;

    @Autowired
    private ExecutorService defaultQueueThreadPool;

    @Autowired
    private TagService tagService;

    /**
     * 创建笔记
     * @param notes
     * @return
     */
    public Result createNotes(Notes notes) {
        try {
            notes.setUserId(jwtAuthService.getLoginUserBean().getUsername());
            Notes insert = this.notesRepository.insert(notes);
            return ResultUtil.success(insert);
        } catch (Exception ex) {
            log.error("新笔记保存异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 更新标签
     * @param tagsBO
     * @return
     */
    public Result upsertNotesTags(TagsBO tagsBO){
        try{
            String userId = jwtAuthService.getLoginUserBean().getUsername();
            String nowDateString = DateUtil.getNowDateString();
            Update update = Update.update("update_time", nowDateString);
            update.set("tags", tagsBO.getTags());
            this.notesRepository.update(tagsBO.getNotesId(), update);
            // 异步处理标签数据
            TagTask tagTask = new TagTask(tagsBO, tagService, userId);
            this.defaultQueueThreadPool.execute(tagTask);
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("更新笔记异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 更新笔记
     * @param notes
     * @return
     */
    public Result updateNotes(Notes notes) {
        try {
            // 更新
            String msg = "更新成功";
            String nowDateString = DateUtil.getNowDateString();
            Update update = Update.update("update_time", nowDateString);
            if (StringUtils.isNotBlank(notes.getTitle())) {
                update.set("title", notes.getTitle());
            }
            if (StringUtils.isNotBlank(notes.getContent())) {
                update.set("content", notes.getContent());
            }
            if (StringUtils.isNotBlank(notes.getCategoryId())) {
                update.set("category_id", notes.getCategoryId());
                msg = "操作成功";
            }
            if (notes.getStar() != null) {
                msg = notes.getStar() == 1 ? "收藏成功" : "已取消收藏";
                update.set("star", notes.getStar());
            }
            if (notes.getDelete() != null) {
                msg = notes.getDelete() == 1 ? "已移动到废纸篓" : "笔记已恢复";
                update.set("delete", notes.getDelete());
            }
            this.notesRepository.update(notes.getId(), update);
            return ResultUtil.success().buildMessage(msg);
        } catch (Exception ex) {
            log.error("更新笔记异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * ID删除
     * @param id
     * @return
     */
    public Result delete(String id){
        try {
            this.notesRepository.remove(id);
            return ResultUtil.success().buildMessage("删除成功");
        } catch (Exception ex) {
            log.error("删除笔记异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 删除全部
     * @return
     */
    public Result deleteAll(){
        try {
            this.notesRepository.removeAll(jwtAuthService.getLoginUserBean().getUsername());
            return ResultUtil.success().buildMessage("废纸篓已清空");
        } catch (Exception ex) {
            log.error("删除全部笔记异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 获取笔记信息
     * @param id
     * @return
     */
    public Result getNotesById(String id) {
        return ResultUtil.success(this.notesRepository.findOne(id));
    }

    /**
     * 查询笔记列表
     * @param notesQuery
     * @return
     */
    public Result getNotesList(NotesQuery notesQuery){
        List<NotesItemVO> vos = new ArrayList<>();
        String userId = jwtAuthService.getLoginUserBean().getUsername();
        Query query = Query.query(Criteria.where("user_id").is(userId));
        // 收藏
        if (notesQuery.getStar() >= 0) {
            query.addCriteria(Criteria.where("star").is(notesQuery.getStar()));
        }
        // 删除
        query.addCriteria(Criteria.where("delete").is(notesQuery.getDelete()));
        // 分类
        if (notesQuery.getLevel() > 0) {
            query.addCriteria(Criteria.where("category_id").is(notesQuery.getCategoryId()));
        }
        // 限制返回数
        if (notesQuery.getLimit() != null && notesQuery.getLimit() > 0) {
            query.limit(notesQuery.getLimit());
        }
        // 排序字段
        String sortBy = "update_time";
        if (StringUtils.isNotBlank(notesQuery.getSortBy())) {
            sortBy = notesQuery.getSortBy();
        }
        // 排序规则
        Sort sort;
        if (notesQuery.getDirection() == null || notesQuery.getDirection() == 0) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }
        query.with(sort);

        // 执行查询
        List<Notes> notesList = this.notesRepository.findList(query);
        for(Notes notes : notesList) {
            NotesItemVO vo = new NotesItemVO();
            BeanUtils.copyProperties(notes, vo);
            vos.add(vo);
        }
        return ResultUtil.success(vos);
    }
}