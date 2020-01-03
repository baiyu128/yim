package com.baiyu.yim.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author baiyu
 * @data 2019-12-30 17:26
 */
@Controller
public class NavigationController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(ModelAndView model) {
        model.setViewName("console/index");
        return model;
    }

    @RequestMapping(value = "/webclient", method = RequestMethod.GET)
    public ModelAndView webclient(ModelAndView model) {
        model.setViewName("console/webclient/index");
        return model;
    }
}
