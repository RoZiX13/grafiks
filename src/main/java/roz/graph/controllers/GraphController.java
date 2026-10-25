package roz.graph.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraphController {

    @GetMapping(value = {"/"})
    public String forward() {
        return "forward:/index.html";
    }
}