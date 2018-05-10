package com.gmail.netcracker.application.dto.dao.imp;

import com.gmail.netcracker.application.dto.dao.interfaces.ChatDao;
import com.gmail.netcracker.application.dto.dao.interfaces.EventDao;
import com.gmail.netcracker.application.dto.model.Chat;
import com.gmail.netcracker.application.dto.model.Event;


import com.gmail.netcracker.application.dto.model.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class ChatDaoImpl extends ModelDao implements ChatDao {

    @Value("${sql.chat.pkColumnName}")
    private String PK_COLUMN_NAME;

    @Value("${sql.chat.add}")
    private String SQL_INSERT;

    @Value("${sql.chat.getChat}")
    private String SQL_GET_CHAT;

    @Value("${sql.chat.delete}")
    private String SQL_DELETE;

    @Value("${sql.chat.getMessages}")
    private String SQL_GET_LIST;

    @Autowired
    private EventDao eventDao;

    private RowMapper<EventMessage> rowMapper;

    private RowMapper<Chat> chatRowMapper;

    private Logger logger = Logger.getLogger(ChatDaoImpl.class.getName());

    protected ChatDaoImpl(DataSource dataSource,
                          @Qualifier("eventMessageRowMapper") RowMapper<EventMessage> rowMapper,
                          @Qualifier("chatRowMapper") RowMapper<Chat> chatRowMapper) {
        super(dataSource);
        this.rowMapper = rowMapper;
        this.chatRowMapper = chatRowMapper;
    }

    @Override
    public Chat getChat(Event event) {
        return findEntity(SQL_GET_CHAT,chatRowMapper,event.getEventId());
    }

    @Override
    public void createChat(Event event) {
        logger.info(event.toString());
        insertEntity(SQL_INSERT, PK_COLUMN_NAME, event.getName(), event.getEventId());
    }

    @Override
    public List<EventMessage> getMessages(Event event) {
        return findEntityList(SQL_GET_LIST, rowMapper, event.getEventId());
    }

    @Override
    public void deleteChat(Event event) {
        deleteEntity(SQL_DELETE, event.getEventId());
    }
}