package com.dz.coop.common.exception;

import com.dz.vo.Ret;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author panqz 2018-10-27 7:28 PM
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Ret error(Exception e) {
        return Ret.error(-1, e.getMessage());
    }

}
