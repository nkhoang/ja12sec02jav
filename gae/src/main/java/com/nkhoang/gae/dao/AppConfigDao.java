package com.nkhoang.gae.dao;

import com.nkhoang.gae.model.AppConfig;

public interface AppConfigDao extends BaseDao<AppConfig, Long> {
   AppConfig getAppConfigByLabel(String label);
}
