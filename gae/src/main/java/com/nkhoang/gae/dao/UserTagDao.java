package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.UserTag;

import java.util.List;

public interface UserTagDao extends BaseDao<UserTag, Long> {
    UserTag get(Long userId, String tagName);

    UserTag save(Long userId, String tagName);

    List<UserTag> getAllUserTags(Long userId);

    List<UserTag> getAll(List<Long> ids);

}
