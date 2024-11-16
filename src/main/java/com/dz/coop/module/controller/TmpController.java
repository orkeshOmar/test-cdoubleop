package com.dz.coop.module.controller;

import com.dz.glory.common.jedis.client.JedisClient;
import com.dz.glory.common.jedis.support.Key;
import com.dz.glory.common.vo.Ret;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author supf@dianzhong.com
 * @date 2024/06/26 13:40
 */
@RestController
@RequestMapping("/tmp")
public class TmpController {

    @Resource
    private JedisClient jedisClient;

    @Autowired
    private com.dz.coop.module.service.MsgService msgService;

    @RequestMapping("/setPartnerId")
    public Object setPartnerId(@RequestParam String partnerId) {
        jedisClient.setString(new Key("SAR", "partner"), partnerId);
        return "success";
    }

    @RequestMapping("/getPartnerId")
    public Object getPartnerId() {
        return jedisClient.getString(new Key("SAR", "partner"));
    }

    @RequestMapping("/startTask")
    @ResponseBody
    public Object startTask(@RequestParam Long cpId) {
        msgService.produceByPartner(cpId);
        return "SUCCESS";
    }

    /**
     * @description  短篇自动更新内容开关
     * @return  java.lang.Object
     */
    @RequestMapping("/updateContentByPartnerSwitch")
    @ResponseBody
    public Object updateContentByPartnerSwitch(@NotBlank String auth, String value) {
        if (auth.equals("RB001")) {
            Key key = new Key("SAR", "switch");
            jedisClient.setString(key, value);
            return Ret.success("key" + key + "成功设置为：" + value);
        }
        return Ret.success("权限异常");
    }
}
