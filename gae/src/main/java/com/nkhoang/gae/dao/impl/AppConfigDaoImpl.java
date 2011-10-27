package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.model.AppConfig;

public class AppConfigDaoImpl extends BaseDaoImpl<AppConfig, Long> {
	public String getClassName() {
		return AppConfig.class.getName();
	}
}
