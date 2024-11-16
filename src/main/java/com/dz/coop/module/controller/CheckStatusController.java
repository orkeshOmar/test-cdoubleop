package com.dz.coop.module.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author supf@dianzhong.com
 * @date 2024/06/29 15:17
 */
@RestController
@RequestMapping("/check/status")
public class CheckStatusController {

    @RequestMapping("/up")
    @ResponseBody
    public Object checkStatus() {
        return "OK";
    }
}
