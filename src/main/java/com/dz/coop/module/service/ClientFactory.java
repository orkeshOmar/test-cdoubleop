package com.dz.coop.module.service;

import com.dz.coop.module.constant.ThirdPart;
import com.dz.coop.module.model.Partner;
import com.dz.coop.module.service.cp.ClientService;
import com.dz.coop.module.service.url.UrlService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service("clientFactory")
public class ClientFactory implements InitializingBean {

    private static Map<Long, ClientService> serviceMap = new HashMap<>();

    private static Map<Long, UrlService> urls = new HashMap<>();

    private static ClientFactory clientFactory;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private PartnerService partnerService;

    @PostConstruct
    public void init() {
        clientFactory = this;
        clientFactory.partnerService = this.partnerService;
    }

    public static ClientService getInstance(Long cpId) {
        ClientService clientService = serviceMap.get(cpId);

        if (clientService != null) {
            return clientService;
        }

        Partner partner = clientFactory.partnerService.getPartnerById(cpId);

        if (partner == null) {
            throw new RuntimeException("cpId: " + cpId + " is not found!");
        }

        // 自有书籍
        if (!partner.isDzStandard() && partner.isInnerDz()) {
            clientService = serviceMap.get(ThirdPart.SONG_SHU_YUE_DU.getCpId());
            // 我方接口规范
        } else if (partner.isDzStandard() && !partner.isInnerDz()) {
            clientService = serviceMap.get(ThirdPart.JING_YUE.getCpId());
        }

        if (clientService == null) {
            throw new RuntimeException("cpId: " + cpId + " is not found!");
        }

        return clientService;
    }

    public static UrlService getUrl(Long cpId) {
        UrlService urlService = urls.get(cpId);

        if (urlService == null) {
            urlService = urls.get(0L);
        }

        return urlService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ClientService> clientServiceMap = applicationContext.getBeansOfType(ClientService.class);
        if (MapUtils.isEmpty(clientServiceMap)) {
            throw new RuntimeException("");
        }
        clientServiceMap.values().forEach(clientService -> {
            Arrays.stream(clientService.getClient()).forEach(clientId -> serviceMap.put(clientId.getCpId(), clientService));
        });

        Map<String, UrlService> urlServiceMap = applicationContext.getBeansOfType(UrlService.class);
        if (MapUtils.isEmpty(urlServiceMap)) {
            throw new RuntimeException("");
        }
        urlServiceMap.values().forEach(urlService -> {
            Arrays.stream(urlService.getClient()).forEach(clientId -> urls.put(clientId.getCpId(), urlService));
        });
    }

}
