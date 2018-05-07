package com.gmail.netcracker.application.service.interfaces;


import com.gmail.netcracker.application.dto.model.Event;
import com.gmail.netcracker.application.dto.model.Item;
import com.gmail.netcracker.application.dto.model.Priority;

import java.util.List;

public interface ItemService {

    void update(Item item);

    void delete(Long itemId);

    void add(Item item);

    List<Item> wishList();

    Item getItem(Long itemId);

    List<Item> findItemByPersonId(Long personId);

    void setPersonId(Item item);

    List<Priority> getAllPriorities();
}
