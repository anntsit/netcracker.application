package com.gmail.netcracker.application.dto.dao.imp;

import com.gmail.netcracker.application.dto.dao.interfaces.ItemDao;
import com.gmail.netcracker.application.dto.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class ItemDaoImpl extends ModelDao implements ItemDao {

    @Value("${sql.item.pkColumnName}")
    private String PK_COLUMN_NAME;

    @Value("${sql.item.add}")
    private String ADD_ITEM;

    @Value("${sql.item.update}")
    private String UPDATE_ITEM;

    @Value("${sql.item.delete}")
    private String DELETE_ITEM;

    @Value("${sql.item.getItem}")
    private String SELECT_ITEM;

    @Value("{sql.item.findItemByName}")
    private String SELECT_ITEM_BY_NAME;

    @Value("${sql.item.findItemByPersonId}")
    private String SQL_FIND_ITEM_BY_PERSON_ID;

    @Value("${sql.item.setRoot}")
    private String SQL_SET_ROOT;

    private final RowMapper<Item> itemRowMapper;

    @Autowired
    public ItemDaoImpl(DataSource dataSource, RowMapper<Item> itemRowMapper) {
        super(dataSource);
        this.itemRowMapper = itemRowMapper;
    }

    @Override
    public Long add(Item item) {
        return insertEntity(ADD_ITEM, PK_COLUMN_NAME,
                item.getPersonId(), item.getName(), item.getDescription(),
                item.getLink(), item.getDueDate(), item.getPriority(), item.getRoot());
    }

    public void setRoot(Long itemId){
        updateEntity(SQL_SET_ROOT, itemId,itemId);
    }

    @Override
    public void update(Item item) {
        updateEntity(UPDATE_ITEM, item.getPersonId(), item.getName(), item.getDescription(),
                item.getLink(), item.getDueDate(), item.getPriority(), item.getItemId());
    }

    @Override
    public void delete(Long itemId) {
        deleteEntity(DELETE_ITEM, itemId);
    }


    @Override
    public Item getItem(Long itemId) {
        return findEntity(SELECT_ITEM, itemRowMapper, itemId);
    }

    @Override
    public List<Item> findItemsByPersonId(Long personId) {
        return jdbcTemplate.query(SQL_FIND_ITEM_BY_PERSON_ID, itemRowMapper, personId);

    }

}

