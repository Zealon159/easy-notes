package cn.zealon.notes.controller;

import cn.zealon.notes.common.base.BaseController;
import cn.zealon.notes.common.result.Result;
import cn.zealon.notes.controller.dto.NotesQuery;
import cn.zealon.notes.controller.dto.TagsBO;
import cn.zealon.notes.domain.Notes;
import cn.zealon.notes.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 笔记接口
 * @author: zealon
 * @since: 2020/12/24
 */
@RestController
@RequestMapping("notes")
public class NotesController extends BaseController {

    @Autowired
    private NotesService notesService;

    @PostMapping("create")
    public Result createNotes(@RequestBody Notes notes) {
        return this.notesService.createNotes(notes);
    }

    @PostMapping("update")
    public Result updateNotes(@RequestBody Notes notes) {
        return this.notesService.updateNotes(notes);
    }

    @PostMapping("update-tags")
    public Result upsertNotesTags(@RequestBody TagsBO tagsBO){
        return this.notesService.upsertNotesTags(tagsBO);
    }

    @PostMapping("delete")
    public Result deleteNotes(@RequestBody Notes notes) {
        return this.notesService.delete(notes.getId());
    }

    @PostMapping("delete-all")
    public Result deleteAll() {
        return this.notesService.deleteAll();
    }

    @GetMapping("details")
    public Result getNotesById(String id) {
        return this.notesService.getNotesById(id);
    }

    @GetMapping("list")
    public Result getNotesList(NotesQuery notesQuery) {
        return this.notesService.getNotesList(notesQuery);
    }
}