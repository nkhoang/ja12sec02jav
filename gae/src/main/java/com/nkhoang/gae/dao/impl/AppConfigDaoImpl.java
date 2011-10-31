package com.nkhoang.gae.dao.impl;

import com.nkhoang.gae.dao.AppConfigDao;
import com.nkhoang.gae.model.AppConfig;
import com.nkhoang.gae.model.Dictionary;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Query;
import java.util.List;

public class AppConfigDaoImpl extends BaseDaoImpl<AppConfig, Long> implements AppConfigDao {
   public String getClassName() {
      return AppConfig.class.getName();
   }

   public AppConfig getAppConfigByLabel(String label) {
      if (StringUtils.isNotEmpty(label)) {
            Query query = entityManager
                    .createQuery("select from " + getClassName() + " u where u.label=:label");
            query.setParameter("label", label);

            List<AppConfig> result = query.getResultList();
            if (result != null && result.size() > 0) {
                return result.get(0);
            }
        }
        return null;
   }
}
