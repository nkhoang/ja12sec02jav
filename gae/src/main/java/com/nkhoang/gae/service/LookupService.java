package com.nkhoang.gae.service;

import com.nkhoang.gae.model.Word;

public interface LookupService {
    Word lookup(String word);

    String getServiceName();
}
