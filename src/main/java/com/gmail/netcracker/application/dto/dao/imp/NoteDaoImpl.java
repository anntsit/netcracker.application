package com.gmail.netcracker.application.dto.dao.imp;

import com.gmail.netcracker.application.dto.dao.interfaces.NoteDao;
import com.gmail.netcracker.application.dto.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class NoteDaoImpl extends ModelDao implements NoteDao {
    @Value("${sql.note.pkColumnName}")
    private String PK_COLUMN_NAME = "note_id";

    @Value("${sql.note.add}")
    private String SQL_ADD;

    @Value("${sql.note.find}")
    private String SQL_FIND;

    @Value("${sql.note.findListNotes}")
    private String SQL_FIND_LIST_NOTES;

    private final RowMapper<Note> noteRowMapper;

    @Autowired
    public NoteDaoImpl(DataSource dataSource,
                       @Qualifier("noteRowMapper") RowMapper<Note> noteRowMapper) {
        super(dataSource);
        this.noteRowMapper = noteRowMapper;
    }

    @Override
    public void insertNote(Note note) {
        insertEntity(SQL_ADD, PK_COLUMN_NAME,
                note.getName(),
                note.getDescription(),
                note.getCreator());
    }

    @Override
    public List<Note> noteList() {
        return findEntityList(SQL_FIND_LIST_NOTES, noteRowMapper);
    }

    @Override
    public Note getNote(int noteId) {
        return findEntity(SQL_FIND, noteRowMapper, noteId);
    }
}
