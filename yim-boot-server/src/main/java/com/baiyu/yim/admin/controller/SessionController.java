package com.baiyu.yim.admin.controller;

import com.baiyu.yim.service.YIMSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author baiyu
 * @data 2019-12-30 17:27
 */
@Controller
@RequestMapping("/console/session")
public class SessionController {

    @Resource
    private YIMSessionService yimSessionService;

    @RequestMapping(value = "/list")
    public String list(Model model) {
        model.addAttribute("sessionList", yimSessionService.list());
        return "console/session/manage";
    }
}
