package cn.zealon.notes.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * 笔记
 * @author: zealon
 * @since: 2020/12/21
 */
public class NotesRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveNotes() {

    }
}
