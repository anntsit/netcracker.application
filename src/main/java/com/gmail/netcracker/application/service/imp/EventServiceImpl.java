package com.gmail.netcracker.application.service.imp;

import com.gmail.netcracker.application.dto.dao.interfaces.*;
import com.gmail.netcracker.application.dto.model.*;
import com.gmail.netcracker.application.service.interfaces.EventService;
import com.gmail.netcracker.application.service.interfaces.FriendService;
import com.gmail.netcracker.application.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {
    private EventDao eventDao;
    private EventTypeDao eventTypeDao;
    private UserService userService;
    private FriendService friendService;
    private PriorityDao priorityDao;
    private NoteDao noteDao;

    @Autowired
    public EventServiceImpl(EventDao eventDao, EventTypeDao eventTypeDao,
                            UserService userService, FriendService friendService, PriorityDao priorityDao, NoteDao noteDao) {
        this.eventDao = eventDao;
        this.eventTypeDao = eventTypeDao;
        this.userService = userService;
        this.friendService = friendService;
        this.priorityDao = priorityDao;
        this.noteDao = noteDao;
    }


    public EventServiceImpl() {

    }

    @Override
    public void update(Event event) {
        setPersonId(event);
        eventDao.update(event);
    }

    @Override
    public void delete(int eventId) {
        eventDao.delete(eventId);
    }

    @Override
    public void insertEvent(Event event) {
        setPersonId(event);
        eventDao.insertEvent(event);
    }

    @Override
    public Event getEvent(int eventId) {
        return eventDao.getEvent(eventId);
    }

    @Override
    public List<Event> eventList() {
        return eventDao.eventList();
    }

    @Override
    public List<Event> findPublicEvents() {
        return eventDao.findPublicEvents();
    }

    @Override
    public List<Event> findPrivateEvents(Long userId) {
        return eventDao.findPrivateEvents(userId);
    }

    @Override
    public List<Event> findFriendsEvents(Long userId) {
        return eventDao.findFriendsEvents(userId);
    }

    @Override
    public List<Event> findDrafts(Long userId) {
        return eventDao.findDrafts(userId);
    }

    @Override
    public List<EventType> getAllEventTypes() {
        return eventTypeDao.getAllEventTypes();
    }

    @Override
    public void setPersonId(Event event) {
        event.setCreator(userService.getAuthenticatedUser().getId());
    }

    @Override
    public List<Event> getAllMyEvents() {
        return eventDao.getAllMyEvents(userService.getAuthenticatedUser().getId());
    }

    @Override
    public void participate(Long userId, long eventId) {
        eventDao.participate(userId, eventId);
    }

    @Override
    public int countParticipants(int eventId) {
        return eventDao.getParticipantsCount(eventId);
    }

    @Override
    public List<User> getParticipants(long eventId) {
        return eventDao.getParticipants(eventId);
    }

    @Override
    public boolean isParticipated(Long id, int eventId) {
        return eventDao.isParticipated(id, eventId) != null;
    }

    @Override
    public void unsubscribe(long id, long eventId) {
        eventDao.unsubscribe(id, eventId);
    }

    @Override
    public int getMaxId() {
        return eventDao.getMaxId();
    }

    @Override
    public boolean allowAccess(Long personId, int eventId) {
        boolean access = false;
        switch (eventDao.getEventType(eventId)) {
            case 1: // private
                access = isCreator(personId, eventId);
                break;
            case 2: // public
                access = true;
                break;
            case 3: // for friends
                access = friendService.getFriendshipById(personId, getCreatorId(eventId)) != null || isCreator(personId, eventId);
                break;
        }
        return access;
    }

    public boolean isCreator(Long personId, int eventId) {
        return eventDao.checkCreatorById(personId, eventId) != null;
    }

    private long getCreatorId(int eventId) {
        return eventDao.getCreator(eventId).getId();
    }

    @Override
    public void setPriority(Integer priority, int eventId, Long user_id) {
        priorityDao.setPriority(priority, eventId, user_id);
    }

    @Override
    public List<Event> myEventsWithPriority() {
        return eventDao.listEventsWithPriority(userService.getAuthenticatedUser().getId());
    }

    @Override
    public Event getMyEventWithPriority(int eventId) {
        return eventDao.getEventWithPriority(userService.getAuthenticatedUser().getId(),
                eventId);
    }

    @Override
    public Participant getParticipation(int eventId) {
        return priorityDao.getParticipant(eventId, userService.getAuthenticatedUser().getId());
    }

    @Override
    public List<Priority> getAllPriorities() {
        return priorityDao.getAllPriority();
    }

    @Override
    public List<Event> findCreatedFriendsEvents(Long id) {
        return eventDao.findCreatedFriendsEvents(id);
    }

    @Override
    public List<Event> findCreatedPublicEvents(Long id) {
        return eventDao.findCreatedPublicEvents(id);
    }

    @Override
    public List<User> subtraction(List<User> minuend, List<User> subtrahend) {
        for (User item : subtrahend) {
            if (minuend.contains(item)) {
                minuend.remove(item);
            }
        }
        return minuend;
    }

    @Override
    public List<User> getFriendsToInvite(Long id, int eventId) {
        List<User> minuend = friendService.getAllFriends(id);
        List<User> subtrahend = getParticipants(eventId);
        return subtraction(minuend, subtrahend);
    }

    @Override
    public List<User> getUsersToInvite(Long currentId, int eventId) {
        List<User> minuend = userService.getAllUsers(currentId);
        List<User> subtrahend = getParticipants(eventId);
        return subtraction(minuend, subtrahend);
    }

    @Override
    @Transactional
    public void transferNoteToEvent(Long noteId, Long userId, Event event) {
        noteDao.delete(noteId);
        event.setCreator(userId);
        eventDao.insertEvent(event);
        eventDao.participate(userId,event.getEventId());
    }
}
