package org.example.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
    @RequestMapping(value = {"/", "index"})
    public String index() {
        return "index";
    }

    @RequestMapping(value = "/show")
    public String show() {
        return "pages/show";
    }

    @RequestMapping(value = "/auth")
    public String auth() {
        return "pages/auth";
    }
}
