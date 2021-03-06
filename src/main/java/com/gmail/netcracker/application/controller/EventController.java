package com.gmail.netcracker.application.controller;

import com.dropbox.core.DbxException;
import com.gmail.netcracker.application.dto.model.*;
import com.gmail.netcracker.application.service.imp.PhotoServiceImp;
import com.gmail.netcracker.application.service.interfaces.*;
import com.gmail.netcracker.application.validation.DraftValidator;
import com.gmail.netcracker.application.validation.ImageValidator;
import com.gmail.netcracker.application.validation.EventValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * This class is a event controller which connects business logic and web view through url patterns.
 */
@Controller
@RequestMapping("/account")
public class EventController {

    @Autowired
    private EventService eventService;
    @Autowired
    private NoteService noteService;
    @Autowired
    private PhotoServiceImp photoService;
    @Autowired
    private UserService userService;
    @Autowired
    private DraftValidator draftValidator;
    @Autowired
    private ChatService chatService;
    @Autowired
    private EventValidator eventValidator;
    @Autowired
    private ImageValidator imageValidator;

    private Logger logger = Logger.getLogger(EventController.class.getName());


    /**
     * This method returns to the web page with fields to be filled in order field to create a new event.
     *
     * @param event
     * @param modelAndView
     * @return modelAndView
     */
    @RequestMapping(value = "/eventList/createNewEvent", method = RequestMethod.GET)
    public ModelAndView createNewEvent(@ModelAttribute(value = "createNewEvent") Event event, ModelAndView modelAndView) {
        event.setPhoto(photoService.getDefaultImageForEvents());
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        modelAndView.setViewName("event/createNewEvent");
        return modelAndView;
    }

    /**
     * This method calls validation for coming to server event fields.
     * Method creates an event  if validation go through  successfully
     * otherwise method returns creation event web page.
     *
     * @param event
     * @param result
     * @param hidden
     * @param photo
     * @param multipartFile
     * @param modelAndView
     * @return modelAndView
     * @throws IOException
     * @throws DbxException
     */
    @RequestMapping(value = "/eventList/createNewEvent", method = RequestMethod.POST)
    public ModelAndView saveNewEvent(@ModelAttribute("createNewEvent") Event event,
                                     BindingResult result,
                                     @RequestParam(value = "hidden") Boolean hidden,
                                     @RequestParam(value = "photoInput") String photo,
                                     @RequestParam(value = "photoFile") MultipartFile multipartFile,
                                     ModelAndView modelAndView) throws IOException, DbxException {
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        modelAndView.setViewName("event/createNewEvent");
        event.setDraft(hidden);
        event.setPhoto(photo);
        if ("".equals(event.getPeriodicity())) {
            event.setPeriodicity(null);
        }
        if (event.getDraft().equals(true)) {
            draftValidator.validate(event, result);
            event.setType((long) 0);
        }
        if (event.getDraft().equals(false)) {
            eventValidator.validate(event, result);
        }
        Boolean imageFormat = imageValidator.validateImageFormat(modelAndView, multipartFile);
        if (result.hasErrors() || imageFormat.equals(false)) {
            return modelAndView;
        }
        if (!multipartFile.isEmpty()) {
            event.setPhoto(photoService.uploadFileOnDropBox(multipartFile, UUID.randomUUID().toString()));
        }
        eventService.insertEvent(event);
        modelAndView.setViewName("redirect:/account/managed");
        return modelAndView;
    }

    /**
     * This method removes the event and redirect to the certain page.
     *
     * @param eventId
     * @return String
     */
    @RequestMapping(value = {"/eventList/deleteEvent-{eventId}"}, method = RequestMethod.GET)
    public String deleteEvent(@PathVariable Long eventId) {
        eventService.delete(eventId);
        return "redirect:/account/managed";
    }

    /**
     * This method transfers to the event web page.
     *
     * @param eventId
     * @param modelAndView
     * @return modelAndView
     */
    @RequestMapping(value = "/eventList/event-{eventId}", method = RequestMethod.GET)
    public ModelAndView viewEvent(@PathVariable("eventId") Long eventId, ModelAndView modelAndView) {
        User authUser = userService.getAuthenticatedUser();
        if (!eventService.allowAccess(authUser.getId(), eventId)) {
            modelAndView.setViewName("accessDenied");
        } else {
            modelAndView.addObject("auth_user", authUser);
            modelAndView.addObject("event", eventService.getEvent(eventId));
            modelAndView.addObject("photo", eventService.getEvent(eventId).getPhoto());
            Logger.getLogger(EventController.class.getName()).info(eventService.getEvent(eventId).getPhoto());
            modelAndView.addObject("user_creator", userService.findUserById(eventService.getEvent(eventId).getCreator()));
            Long participants = eventService.countParticipants(eventId);
            modelAndView.addObject("participants", participants);
            Boolean isParticipated = eventService.isParticipantOfEvent(authUser.getId(), eventId);
            modelAndView.addObject("participation", eventService.getParticipation(eventId));
            modelAndView.addObject("isParticipated", isParticipated);
            modelAndView.addObject("priorities", eventService.getAllPriorities());
            modelAndView.addObject("chatWithCreator", chatService.getChatByEventId(eventService.getEvent(eventId), true));
            modelAndView.addObject("chatWithOutCreator", chatService.getChatByEventId(eventService.getEvent(eventId), false));
            modelAndView.setViewName("event/viewEvent");
        }
        return modelAndView;
    }

    /**
     * This method allows to edit event priority.
     *
     * @param eventId
     * @param participation
     * @param model
     * @return String
     */
    @RequestMapping(value = "/eventList/event-{eventId}", method = RequestMethod.POST)
    public String editPriority(@PathVariable("eventId") Long eventId,
                               @ModelAttribute(value = "participation") Participant participation,
                               Model model) {

        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        eventService.setPriority(participation.getPriority(), eventId, userService.getAuthenticatedUser().getId());
        return "redirect:/account/eventList/event-" + eventId;
    }

    /**
     * This method returns event fields update web page.
     *
     * @param eventId
     * @param modelAndView
     * @return modelAndView
     */
    @RequestMapping(value = {"/eventList/editevent-{eventId}"}, method = RequestMethod.GET)
    public ModelAndView editEvent(@PathVariable Long eventId,
                                  ModelAndView modelAndView) {
        modelAndView.addObject("editEvent", eventService.getEvent(eventId));
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        modelAndView.setViewName("event/updateEvent");
        return modelAndView;
    }

    /**
     * This method calls validation for coming to server events fields.
     * Method updates an event if validation go through  successfully
     * otherwise method returns updating event web page.
     *
     * @param event
     * @param multipartFile
     * @param photo
     * @param result
     * @param modelAndView
     * @return modelAndView
     * @throws IOException
     * @throws DbxException
     */
    @RequestMapping(value = {"/eventList/editevent-{eventId}"}, method = RequestMethod.POST)
    public ModelAndView updateEvent(@ModelAttribute("editEvent") Event event,
                                    @RequestParam(value = "photoFile") MultipartFile multipartFile,
                                    @RequestParam(value = "photo") String photo,
                                    BindingResult result,
                                    ModelAndView modelAndView) throws IOException, DbxException {
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        event.setType(event.getTypeId());
        if ("".equals(event.getPeriodicity())) {
            event.setPeriodicity(null);
        }
        if (event.getDraft().equals(true)) {
            draftValidator.validate(event, result);
            event.setType((long) 0);
            event.setPeriodicity(null);
        } else {
            eventValidator.validate(event, result);
        }
        Boolean imageFormat = imageValidator.validateImageFormat(modelAndView, multipartFile);
        if (result.hasErrors() || imageFormat.equals(false)) {
            modelAndView.setViewName("event/updateEvent");
            return modelAndView;
        }
        if (!multipartFile.isEmpty()) {
            if (!event.getPhoto().equals(photoService.getDefaultImageForEvents())) {
                photoService.deleteFile(event.getPhoto());
            }
            event.setPhoto(photoService.uploadFileOnDropBox(multipartFile, UUID.randomUUID().toString()));
        }
        modelAndView.setViewName("event/updateEvent");
        logger.info(event.toString());
        eventService.update(event);
        if (!event.getDraft()) {
            modelAndView.setViewName("redirect:/account/managed");
        } else {
            modelAndView.setViewName("redirect:/account/draft");
        }
        return modelAndView;
    }

    /**
     * This method allows to subscribe the event.
     *
     * @param eventId
     * @param model
     * @return String
     */
    @RequestMapping(value = "/participate", method = RequestMethod.POST)
    public String participate(@RequestParam(value = "event_id") Long eventId, Model model) {
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        eventService.participate(userService.getAuthenticatedUser().getId(), eventId);
        return "redirect:/account/eventList/event-" + eventId;
    }

    /**
     * This method returns event types to the web page.
     *
     * @return List<EventType>
     */
    @ModelAttribute("eventTypes")
    public List<EventType> getAllEventTypes() {
        return eventService.getAllEventTypes();
    }

    /**
     * This method returns a web page with the list of event subscribes.
     *
     * @param eventId
     * @param model
     * @return String
     */
    @RequestMapping(value = "/event-{eventId}/participants", method = RequestMethod.GET)
    public String getParticipants(@PathVariable(value = "eventId") Long eventId, Model model) {
        List<User> participantList = eventService.getParticipants(eventId);
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        model.addAttribute("participantList", participantList);
        return "event/participants";
    }

    /**
     * This method returns available web page.
     *
     * @param model
     * @return String
     */
    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public String available(Model model) {
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        model.addAttribute("friendsEventList", eventService.findAvailableEvents());
        return "event/available";
    }

    /**
     * This method allows to unsubscribe the event.
     *
     * @param eventId
     * @param model
     * @return String
     */
    @RequestMapping(value = "/unsubscribe", method = RequestMethod.POST)
    public String unsubscribe(@RequestParam(value = "event_id") Long eventId, Model model) {
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        eventService.unsubscribe(userService.getAuthenticatedUser().getId(), eventId);
        return "redirect:/account/eventList/event-" + eventId;
    }

    /**
     * This method returns a web page where customer can see own subscribes.
     *
     * @param model
     * @return String
     */
    @RequestMapping(value = "/subscriptions", method = RequestMethod.GET)
    public String getSubscriptions(Model model) {
        List<Event> eventList = eventService.findEventSubscriptions();
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        model.addAttribute("eventList", eventList);
        if (eventList.isEmpty()) model.addAttribute("message", "You have not any subscription");
        else model.addAttribute("message", "You are subscribed on following events :");
        return "event/subscriptions";
    }

    /**
     * This method returns a web page with the list of customer drafts.
     *
     * @param model
     * @return String
     */
    @RequestMapping(value = "/draft", method = RequestMethod.GET)
    public String draft(Model model) {
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        List<Event> draftList = eventService.findDrafts(userService.getAuthenticatedUser().getId());
        model.addAttribute("draftList", draftList);
        if (draftList.isEmpty()) model.addAttribute("message", "You have not any drafts");
        else model.addAttribute("message", "Your drafts :");
        return "event/draft";
    }

    /**
     * This method returns a web page where customer can see own events.
     *
     * @param model
     * @return String
     */
    @RequestMapping(value = "/managed", method = RequestMethod.GET)
    public String managed(Model model) {
        List<Event> publicEventList = eventService.findCreatedPublicEvents(userService.getAuthenticatedUser().getId());
        List<Event> privateEventList = eventService.findPrivateEvents(userService.getAuthenticatedUser().getId());
        List<Event> friendsEventList = eventService.findCreatedFriendsEvents(userService.getAuthenticatedUser().getId());
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        model.addAttribute("publicEventList", publicEventList);
        model.addAttribute("friendsEventList", friendsEventList);
        model.addAttribute("privateEventList", privateEventList);

        if (publicEventList.isEmpty() && friendsEventList.isEmpty() && privateEventList.isEmpty()) {
            model.addAttribute("message", "You have not created any event");
        } else model.addAttribute("message", "Created events :");
        return "event/managed";
    }

    /**
     * This method allows customer invite friends into public event.
     *
     * @param model
     * @param eventId
     * @return String
     */
    @RequestMapping(value = "/public/event-{eventId}/invite", method = RequestMethod.GET)
    public String inviteListToPublic(Model model, @PathVariable(value = "eventId") Long eventId) {
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        List<User> usersToInvite = eventService.findFriendsForInvite(userService.getAuthenticatedUser().getId(), eventId);
        model.addAttribute("usersToInvite", usersToInvite);
        String message = usersToInvite.size() > 0 ? "Invite users" : "All users are subscribed on this event";
        model.addAttribute("message", message);
        model.addAttribute("eventId", eventId);
        return "/event/inviteToPublicEvent";
    }

    /**
     * This method save customer friend to event as participants.
     *
     * @param eventId
     * @param userId
     * @return String
     */
    @RequestMapping(value = "/{eventId}/invite-to-public", method = RequestMethod.POST)
    public String inviteToPublic(@PathVariable(value = "eventId") Long eventId,
                                 @RequestParam(value = "userId") Long userId) {
        eventService.participate(userId, eventId);
        return "redirect:/account/public/event-" + eventId + "/invite";
    }

    @RequestMapping(value = "{eventId}/invite-for-friends", method = RequestMethod.POST)
    public String inviteToForFriends(@PathVariable(value = "eventId") Long eventId,
                                 @RequestParam(value = "userId") Long userId) {
        eventService.participate(userId, eventId);
        return "redirect:/account/for-friends/event-" + eventId + "/invite";
    }

    /**
     * This method allows customer invite friends into for_friend  event.
     *
     * @param model
     * @param eventId
     * @return String
     */
    @RequestMapping(value = "/for-friends/event-{eventId}/invite", method = RequestMethod.GET)
    public String inviteToForFriends(Model model, @PathVariable(value = "eventId") Long eventId) {
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        List<User> friendsToInvite = eventService.findFriendsForInvite(userService.getAuthenticatedUser().getId(), eventId);
        model.addAttribute("friendsToInvite", friendsToInvite);
        String message = friendsToInvite.size() > 0 ? "Invite users" : "All your friends are subscribed on this event";
        model.addAttribute("message", message);
        model.addAttribute("eventId", eventId);
        return "/event/inviteToEventForFriends";
    }

    /**
     * This method returns a web page where customer can transform note into event.
     *
     * @param event
     * @param modelAndView
     * @param noteId
     * @return modelAndView
     */
    @RequestMapping(value = {"/translateToEvent-{noteId}"}, method = RequestMethod.GET)
    public ModelAndView translateToEvent(@ModelAttribute(value = "createNewEvent") Event event,
                                         ModelAndView modelAndView,
                                         @PathVariable(value = "noteId") Long noteId) {
        event.setPhoto(photoService.getDefaultImageForEvents());
        logger.info(event.getPhoto());
        Note note = noteService.getNote(noteId);
        event.setName(note.getName());
        event.setDescription(note.getDescription());
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        modelAndView.setViewName("event/createNewEvent");
        return modelAndView;
    }

    /**
     * This method calls validation for coming to server events fields.
     * Method saves an event and delete note if validation go through  successfully
     * otherwise method returns transformer note to event web page.
     *
     * @param event
     * @param result
     * @param photo
     * @param multipartFile
     * @param modelAndView
     * @param noteId
     * @return modelAndView
     * @throws IOException
     * @throws DbxException
     */
    @RequestMapping(value = {"/translateToEvent-{noteId}"}, method = RequestMethod.POST)
    public ModelAndView saveNoteToEvent(@ModelAttribute("createNewEvent") Event event,
                                        BindingResult result,
                                        @RequestParam(value = "photoInput") String photo,
                                        @RequestParam(value = "photoFile") MultipartFile multipartFile,
                                        ModelAndView modelAndView,
                                        @PathVariable Long noteId,
                                        @RequestParam(value = "hidden") Boolean hidden) throws IOException, DbxException {
        modelAndView.setViewName("event/createNewEvent");
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        event.setDraft(hidden);
        event.setPhoto(photo);
        if (event.getPeriodicity().equals("")) {
            event.setPeriodicity(null);
        }
        if (event.getDraft().equals(true)) {
            draftValidator.validate(event, result);
            event.setType((long) 0);
        }
        if (event.getDraft().equals(false)) {
            eventValidator.validate(event, result);

        }
        Boolean imageFormat = imageValidator.validateImageFormat(modelAndView, multipartFile);
        if (result.hasErrors() || imageFormat.equals(false)) {
            return modelAndView;
        }
        if (!multipartFile.isEmpty()) {
            photoService.uploadFileOnDropBox(multipartFile, UUID.randomUUID().toString());
        }
        logger.info(event.getPhoto());
        eventService.convertNoteToEvent(noteId, userService.getAuthenticatedUser().getId(), event);
        modelAndView.setViewName("redirect:/account/managed");
        return modelAndView;
    }

    /**
     * This method returns a web page where customer can convert draft to event.
     *
     * @param eventId
     * @param modelAndView
     * @return modelAndView
     */
    @RequestMapping(value = {"/eventList/convertToEvent-{eventId}"}, method = RequestMethod.GET)
    public ModelAndView getPageConvertDraftToEvent(@PathVariable Long eventId,
                                                   ModelAndView modelAndView) {
        Event event = eventService.getEvent(eventId);
        event.setPhoto(photoService.getDefaultImageForEvents());
        modelAndView.addObject("editEvent", event);
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        modelAndView.setViewName("event/updateEvent");
        return modelAndView;
    }

    /**
     * This method calls validation for coming to server events fields.
     * Method saves an event and delete draft if validation go through  successfully
     * otherwise method returns transformer draft to event web page.
     *
     * @param eventId
     * @param event
     * @param photo
     * @param multipartFile
     * @param result
     * @param modelAndView
     * @return modelAndView
     * @throws IOException
     * @throws DbxException
     */
    @RequestMapping(value = {"/eventList/convertToEvent-{eventId}"}, method = RequestMethod.POST)
    public ModelAndView convertDraftToEvent(@PathVariable Long eventId,
                                            @ModelAttribute("editEvent") Event event,
                                            @RequestParam(value = "photo") String photo,
                                            @RequestParam(value = "photoFile") MultipartFile multipartFile,
                                            BindingResult result,
                                            ModelAndView modelAndView) throws IOException, DbxException {
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        event.setPhoto(photo);
        event.setType(event.getTypeId());

        modelAndView.setViewName("event/updateEvent");
        if ("".equals(event.getPeriodicity())) {
            event.setPeriodicity(null);
        }
        eventValidator.validate(event, result);
        Boolean imageFormat = imageValidator.validateImageFormat(modelAndView, multipartFile);
        if (imageFormat.equals(false) || result.hasErrors()) {
            return modelAndView;
        }
        if (!multipartFile.isEmpty()) {
            photoService.uploadFileOnDropBox(multipartFile, UUID.randomUUID().toString());
        }
        eventService.convertDraftToEvent(event);
        modelAndView.setViewName("redirect:/account/managed");
        return modelAndView;
    }

}
