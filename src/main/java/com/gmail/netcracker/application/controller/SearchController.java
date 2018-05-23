package com.gmail.netcracker.application.controller;

import com.gmail.netcracker.application.dto.model.User;
import com.gmail.netcracker.application.service.interfaces.FriendService;
import com.gmail.netcracker.application.service.interfaces.SearchService;
import com.gmail.netcracker.application.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/account")
public class SearchController {

    @Autowired
    private UserService userService;
    @Autowired
    private SearchService searchService;
    @Autowired
    private FriendService friendService;


    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView mainSearch(ModelAndView modelAndView,
                                   @RequestParam("search") String search,
                                   @RequestParam("typeSearch") String typeSearch,
                                   RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("search", search);
        if (typeSearch.equals("user")) {
            modelAndView.setViewName("redirect: /account/search/user");
            return modelAndView;
        }
//        if("friend".equals(type)) modelAndView.setViewName("redirect: /account/search
        if (typeSearch.equals("event")) {
            modelAndView.setViewName("redirect: /account/eventList/search");
            return modelAndView;
        }

//        if("myEvent".equals(type)) modelAndView.setViewName("redirect: /account/search/myEvent");
        if (typeSearch.equals("item")) {
            modelAndView.setViewName("redirect: /account/friends/search");
            return modelAndView;
        }
        return modelAndView;
    }

    @RequestMapping(value = "/eventList/search", method = RequestMethod.GET)
    public String getSearchEventString(String search)
    {
        Model model = (Model) new ModelAndView();
        if (search == null || search.isEmpty()) return "redirect:/account/eventList";
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        model.addAttribute("resultSearchPublic", searchService.searchPublicEvents(search, userService.getAuthenticatedUser()));
        model.addAttribute("resultSearchUserEvents", searchService.searchUserEvents(search, userService.getAuthenticatedUser()));
        return "event/resultSearch";
    }

    @RequestMapping(value = "/items/search", method = RequestMethod.GET)
    public String getSearchItem(Model model, String search) {
        if (search == null || search.isEmpty())
            return "redirect:/account/user-" + userService.getAuthenticatedUser().getId() + "/wishList";
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        model.addAttribute("resultSearchItem", searchService.searchItems(search, userService.getAuthenticatedUser()));
        return "item/resultSearch";
    }

    @RequestMapping(value = "/friends/search", method = RequestMethod.GET)
    public String getSearchUser(Model model, String search) {
        if (search == null || search.isEmpty()) {
            return "redirect:/account/friends";
        }
        model.addAttribute("auth_user", userService.getAuthenticatedUser());
        List<User> friendList = friendService.searchFriends(userService.getAuthenticatedUser().getId(), search);
        model.addAttribute("friendList", friendList);
        List<User> subtractionUsers = friendService.subtractionFromFriendList(friendService.searchUsers(userService.getAuthenticatedUser().getId(), search));
        model.addAttribute("subtractionUsers", subtractionUsers);
        return "friend/friends";
    }
}