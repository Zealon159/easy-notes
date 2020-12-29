package cn.zealon.notes.service;

import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.common.result.ResultUtil;
import cn.zealon.notes.common.utils.DateUtil;
import cn.zealon.notes.controller.dto.NotesQuery;
import cn.zealon.notes.domain.Notes;
import cn.zealon.notes.vo.NotesItemVO;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * 笔记服务
 * @author: zealon
 * @since: 2020/12/24
 */
@Slf4j
@Service
public class NotesService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Result createNotes(Notes notes) {
        try {
            String nowDateString = DateUtil.getNowDateString();
            notes.setCreateTime(nowDateString);
            notes.setUpdateTime(nowDateString);
            notes.setStar(0);
            notes.setDelete(0);
            notes.setCategoryId(notes.getCategoryId());
            notes.setUserId("zealon");
            this.mongoTemplate.insert(notes);
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("新笔记保存异常!", ex);
            return ResultUtil.fail();
        }
    }

    public Result upsertNotesTags(List<String> tags, String id){
        try{
            String nowDateString = DateUtil.getNowDateString();
            Update update = Update.update("update_time", nowDateString);
            update.set("tags", tags);
            Query query = Query.query(Criteria.where("_id").is(id));
            this.mongoTemplate.updateFirst(query, update, Notes.class);
            return ResultUtil.success();
        } catch (Exception ex) {
            log.error("更新笔记异常!", ex);
            return ResultUtil.fail();
        }
    }

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
            if (notes.getStar() != null) {
                msg = notes.getStar() == 1 ? "收藏成功" : "已取消收藏";
                update.set("star", notes.getStar());
            }
            if (notes.getDelete() != null) {
                msg = notes.getDelete() == 1 ? "已移动到废纸篓" : "笔记已恢复";
                update.set("delete", notes.getDelete());
            }
            Query query = Query.query(Criteria.where("_id").is(notes.getId()));
            this.mongoTemplate.updateFirst(query, update, Notes.class);
            return ResultUtil.success().buildMessage(msg);
        } catch (Exception ex) {
            log.error("更新笔记异常!", ex);
            return ResultUtil.fail();
        }
    }

    public Result delete(String id){
        try {
            Query query = Query.query(Criteria.where("_id").is(id));
            this.mongoTemplate.remove(query, "notes");
            return ResultUtil.success().buildMessage("删除成功");
        } catch (Exception ex) {
            log.error("删除记保存异常!", ex);
            return ResultUtil.fail();
        }
    }

    /**
     * 获取笔记信息
     * @param id
     * @return
     */
    public Result getNotesById(String id) {
        return ResultUtil.success(this.getNotes(id));
    }

    public Result getNotesList(NotesQuery notesQuery){
        List<NotesItemVO> vos = new ArrayList<>();
        Query query = Query.query(Criteria.where("user_id").is(notesQuery.getUserId()));
        // 收藏
        if (notesQuery.getStar() >= 0) {
            query.addCriteria(Criteria.where("star").is(notesQuery.getStar()));
        }
        // 删除
        query.addCriteria(Criteria.where("delete").is(notesQuery.getDelete()));
        // 分类
        if (notesQuery.getLevel() > 0) {
            String categoryField = notesQuery.getLevel() == 1 ? "category_id" : "category_sub_id";
            query.addCriteria(Criteria.where(categoryField).is(notesQuery.getCategoryId()));
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
        List<Notes> notesList = this.mongoTemplate.find(query, Notes.class);
        for(Notes notes : notesList) {
            NotesItemVO vo = new NotesItemVO();
            BeanUtils.copyProperties(notes, vo);
            vos.add(vo);
        }
        return ResultUtil.success(vos);
    }

    private Notes getNotes(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return this.mongoTemplate.findOne(query, Notes.class);
    }
}