package cn.zealon.notes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主页控制器
 * @author: zealon
 * @since: 2020/11/16
 */
@RestController
public class IndexController {

    @GetMapping("/home")
    public String hello(String word){
        return "hello," + word;
    }
}
