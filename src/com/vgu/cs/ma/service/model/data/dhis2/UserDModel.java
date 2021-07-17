package com.vgu.cs.ma.service.model.data.dhis2;

import com.vgu.cs.common.util.CollectionUtils;
import com.vgu.cs.common.util.HttpUtils;
import com.vgu.cs.common.util.StringUtils;
import com.vgu.cs.engine.entity.dhis2.model.User;
import com.vgu.cs.engine.entity.dhis2.model.Users;

import java.util.ArrayList;
import java.util.List;

public class UserDModel extends Dhis2BaseDModel {

    public static final UserDModel INSTANCE = new UserDModel();

    private UserDModel() {

    }

    /**
     * Paging is not yet supported, only the first 50 results are returned
     *
     * @return List of User's known to the current user
     */
    public List<User> getUsers() {
        Users users = getJsonList("users", Users.class);
        if (CollectionUtils.isNullOrEmpty(users.getUsers())) {
            return new ArrayList<>();
        }

        return users.getUsers();
    }

    public User getUser(String id) {
        String url = BASE_URL + "userLookup/" + id + ".json";
        String response = HttpUtils.sendGet(url, prepareHeaderWithBasicAuth());
        if (StringUtils.isNullOrEmpty(response)) {
            return null;
        }

        return GSON.fromJson(response, User.class);
    }
}
