package com.vgu.cs.ma.service.model.data.dhis2;

/*
 * Copyright (c) 2012-2016 by Zalo Group.
 * All Rights Reserved.
 *
 * @author namnh16 on 13/05/2021
 */

import com.google.gson.Gson;
import com.vgu.cs.common.util.HttpUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.dhis2.model.BaseDhis2Entity;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public abstract class Dhis2BaseModel {

    protected final String BASE_URL = "https://play.dhis2.org/2.36.0/api/";
    protected final String USERNAME = "admin";
    protected final String PASSWORD = "district";
    protected final Gson GSON = new Gson();

    protected <T extends BaseDhis2Entity> T getJsonList(String resourceType, Class<T> tClass) {
        return getJsonList(resourceType, new HashMap<>(), tClass);
    }

    protected <T extends BaseDhis2Entity> T getJsonList(String resourceType, Map<String, String> query, Class<T> tClass) {
        query.put("fields", "*");

        StringBuilder urlBuilder = new StringBuilder(BASE_URL)
                .append(resourceType)
                .append(".json")
                .append("?");

        for (Map.Entry<String, String> entry : query.entrySet()) {
            urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        urlBuilder.deleteCharAt(urlBuilder.length() - 1);

        String response = HttpUtils.sendGet(urlBuilder.toString(), prepareHeaderWithBasicAuth());
        if (StringUtils.isNullOrEmpty(response)) {
            return null;
        }

        return GSON.fromJson(response, tClass);
    }

    protected Map<String, String> prepareHeaderWithBasicAuth() {
        Map<String, String> headers = new HashMap<>();
        headers.put(
                "Authorization",
                "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", USERNAME, PASSWORD).getBytes())
        );

        return headers;
    }
}
