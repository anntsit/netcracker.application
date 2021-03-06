package com.gmail.netcracker.application.controller;

import com.gmail.netcracker.application.dto.model.Event;
import com.gmail.netcracker.application.service.interfaces.*;
import com.gmail.netcracker.application.utilites.Filter;
import com.google.gson.Gson;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(value = "/account")
public class CalendarController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private FilterService filterService;

    @Autowired
    private EventRangeService eventRangeService;

    @Autowired
    private Gson gsonEvents;

    @Autowired
    private Gson gsonTimeline;

    @Autowired
    private FriendService friendService;

    /**
     * This method returns a events to Calendar taking into filter.
     *
     * @param start
     * @param end
     * @param jsonPriority
     * @param jsonTypes
     * @return
     */
    @RequestMapping(value = "/getEventsWithFilter", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String calendarRangeWithFilter(@RequestParam("start") Long start,
                                @RequestParam("end") Long end,
                                @RequestParam("filterPriority") String jsonPriority,
                                @RequestParam("filterTypes") String jsonTypes){
        List<Event> eventList = eventRangeService.getEventsFromRange(userService.getAuthenticatedUser().getId(), start, end);
        eventList = filterService.filterOfPriority(Arrays.asList(gsonEvents.fromJson(jsonPriority, Long[].class)), eventList);
        eventList = filterService.filterOfType(Arrays.asList(gsonEvents.fromJson(jsonTypes, Long[].class)), eventList);
        return gsonEvents.toJson(eventList);
    }

    /**
     * This method returns a events to Calendar.
     *
     * @param start
     * @param end
     * @return
     */
    @RequestMapping(value = "/getEvents", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String calendarRange(@RequestParam("start") Long start,
                                @RequestParam("end") Long end){
        return gsonEvents.toJson(eventRangeService.getEventsFromRange(userService.getAuthenticatedUser().getId(), start, end));
    }

    /**
     * This method returns a events to Timeline taking into checked friends.
     *
     * @param jsonCheckedFriends
     * @param start
     * @param end
     * @return
     */
    @RequestMapping(value = "/getTimeline", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getTimeline(@RequestParam("checkedFriends") String jsonCheckedFriends,
                              @RequestParam("start") Long start,
                              @RequestParam("end") Long end){
        List<Long> checkedFriends = Arrays.asList(gsonEvents.fromJson(jsonCheckedFriends, Long[].class));
        return gsonTimeline.toJson(eventRangeService.getEventsFromRange(checkedFriends, start, end));
    }

    /**
     * This method returns a calendar web page
     *
     * @param modelAndView
     * @return
     */
    @RequestMapping(value = "/calendar", method = RequestMethod.GET)
    public ModelAndView calendarHome(ModelAndView modelAndView) {
        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        modelAndView.addObject("priorities", eventService.getAllPriorities());
        modelAndView.addObject("eventTypes", eventService.getAllEventTypes());
        modelAndView.addObject("filter", new Filter());
        modelAndView.setViewName("calendar/calendar");
        return modelAndView;
    }

    /**
     * This method returns a calendar web page after checked filter.
     *
     * @param filter
     * @param result
     * @param modelAndView
     * @return
     */
    @RequestMapping(value = "/calendar", method = RequestMethod.POST)
    public ModelAndView calendarWithFilter(@ModelAttribute("filter") Filter filter,
                                           BindingResult result,
                                           ModelAndView modelAndView) {

        modelAndView.addObject("auth_user", userService.getAuthenticatedUser());
        modelAndView.addObject("priorities", eventService.getAllPriorities());
        modelAndView.addObject("eventTypes", eventService.getAllEventTypes());
        modelAndView.addObject("filter", filter);
        modelAndView.setViewName("calendar/calendar");
        return modelAndView;
    }

    /**
     * This method returns a timeline web page.
     *
     * @param model
     * @return String
     */
    @RequestMapping(value = "/timeline", method = RequestMethod.GET)
    public String timeline(Model model) {
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        model.addAttribute("list_friends", friendService.getAllFriends(userService.getAuthenticatedUser().getId()));
        model.addAttribute("filter", new Filter());
        return "calendar/timeline";
    }

    /**
     * This method returns a timeline web page after checked friends.
     *
     * @param model
     * @return String
     */
    @RequestMapping(value = "/timeline", method = RequestMethod.POST)
    public String timelinePost(Model model,
                               @ModelAttribute("filter") Filter filter) {
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        model.addAttribute("list_friends", friendService.getAllFriends(userService.getAuthenticatedUser().getId()));
        model.addAttribute("filter", filter);
        return "calendar/timeline";
    }
}
