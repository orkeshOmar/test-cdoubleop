package com.dz.coop.common.aop;

import com.dz.coop.common.annotation.AccessLimit;
import com.dz.coop.common.exception.BookException;
import com.dz.coop.common.util.CheckUtil;
import com.dz.coop.common.util.EscapeUtil;
import com.dz.coop.module.constant.ICacheRedisKey;
import com.dz.coop.module.model.Partner;
import com.dz.jedis.client.JedisClient;
import com.dz.tools.TraceKeyHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Aspect
public class LogAop {

    private static final Logger logger = LoggerFactory.getLogger(LogAop.class);

    @Resource
    private JedisClient newJedisClient;

    @Pointcut("execution(* com.dz.coop.module.service.cp.impl..getBookList(..))")
    public void booksPointcut() {
    }

    @Pointcut("execution(* com.dz.coop.module.service.cp.impl..getBookInfo(..))")
    public void bookPointcut() {
    }

    @Pointcut("execution(* com.dz.coop.module.service.cp.impl..getVolumeList(..))")
    public void chaptersPointcut() {
    }

    @Pointcut("execution(* com.dz.coop.module.service.cp.impl..getCPChapterInfo(..))")
    public void chapterPointcut() {
    }

    @Around("booksPointcut()")
    public Object books(ProceedingJoinPoint joinPoint) throws BookException {
        return log(joinPoint, (objects) -> {

            Partner partner = (Partner) objects[0];

            return String.format("[%s][%s]书籍列表获取", TraceKeyHolder.getUserKey("prefix"), partner.getName());
        });
    }

    @Around("bookPointcut()")
    public Object book(ProceedingJoinPoint joinPoint) throws BookException {
        return log(joinPoint, (objects) -> String.format("%s书籍详情获取", TraceKeyHolder.getUserKey("prefix")));
    }

    @Around("chaptersPointcut()")
    public Object chapters(ProceedingJoinPoint joinPoint) throws BookException {
        return log(joinPoint, (objects) -> String.format("%s章节列表获取", TraceKeyHolder.getUserKey("prefix")));
    }

    @Around("chapterPointcut()")
    public Object chapter(ProceedingJoinPoint joinPoint) throws BookException {
        return log(joinPoint, (objects) -> String.format("%s[volumeId=%s][cpChapterId=%s]章节内容获取", TraceKeyHolder.getUserKey("prefix"), objects[2], objects[3]));
    }

    private Object log(ProceedingJoinPoint joinPoint, Function<Object[], String> function) throws BookException {
        Object[] args = joinPoint.getArgs();
        String prefix = function.apply(args);

        try {
            Object target = joinPoint.getTarget();
            if (target != null) {
                Class<?>[] clazz = target.getClass().getInterfaces();
                Class<?>[] parameterTypes = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameterTypes();
                String methodName = joinPoint.getSignature().getName();

                if (clazz != null && parameterTypes != null && StringUtils.isNotBlank(methodName)) {
                    Partner owchPartner = (Partner) args[0];
                    Long cpId = owchPartner.getId();

                    for (Class<?> c : clazz) {
                        try {
                            Method method = c.getMethod(methodName, parameterTypes);
                            if (method != null) {
                                AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);
                                if (accessLimit != null) {
                                    long[] cpPartnerIds = accessLimit.cpPartnerIds();
                                    List<Long> cpIds = Arrays.stream(cpPartnerIds).boxed().collect(Collectors.toList());

                                    if (CollectionUtils.isNotEmpty(cpIds) && cpIds.contains(cpId)) {
                                        int position = cpIds.indexOf(cpId);
                                        int limitSecond = accessLimit.limitSecond()[position];
                                        int limitCount = accessLimit.limitCount()[position];
                                        long sleepMs = accessLimit.sleepMillisecond()[position];

                                        if (sleepMs > 0) {
                                            logger.info("[cpId={}][{}]接口访问休眠{}毫秒......", cpId, owchPartner.getName(), sleepMs);
                                            Thread.sleep(sleepMs);
                                        } else {
                                            String limitCpKey = String.format(ICacheRedisKey.LIMIT_CP_KEY, cpId);

                                            Long count = newJedisClient.incr(ICacheRedisKey.DB_1, limitCpKey);
                                            logger.info("[cpId={}][{}]当前接口访问次数：{}", cpId, owchPartner.getName(), count);
                                            if (count == 1) {
                                                newJedisClient.expire(ICacheRedisKey.DB_1, limitCpKey, limitSecond);
                                            }

                                            if (count >= limitCount) {
                                                logger.info("[cpId={}][{}]接口访问次数超限，限制为{}秒{}次，休眠4秒......", cpId, owchPartner.getName(), limitSecond, limitCount);
                                                Thread.sleep(4000);
                                            }
                                        }
                                    }

                                    break;
                                }
                            }
                        } catch (NoSuchMethodException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }

            Object ret = joinPoint.proceed();
            String url = TraceKeyHolder.getUserKey("url");

            if (ret == null) {
                throw new BookException(prefix + "异常\n访问url=" + url);
            }

            if (ret instanceof List) {
                List retList = (List) ret;
                if (CollectionUtils.isEmpty(retList)) {
                    throw new BookException(prefix + "异常\n访问url=" + url);
                }
            }

            try {
                CheckUtil.check(ret);
            } catch (BookException e) {
                throw new BookException(prefix + "异常," + e.getMessage() + "\n访问url=" + url);
            }

            EscapeUtil.escape(ret);

            logger.info(prefix + "成功");

            return ret;

        } catch (Throwable e) {
            throw new BookException(function.apply(args) + "异常\n访问url=" + TraceKeyHolder.getUserKey("url") + "\n返回结果ret=" + StringEscapeUtils.unescapeJava(TraceKeyHolder.getUserKey("ret")), e);
        }
    }

}
