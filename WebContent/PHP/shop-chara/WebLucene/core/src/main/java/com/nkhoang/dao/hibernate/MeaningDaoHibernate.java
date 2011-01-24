package com.nkhoang.dao.hibernate;

import com.nkhoang.dao.MeaningDao;
import com.nkhoang.model.Meaning;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: 1/23/11
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
@Repository("meaningDao")
public class MeaningDaoHibernate extends GenericDaoHibernate<Meaning, Long> implements MeaningDao{

    public MeaningDaoHibernate() {
        super(Meaning.class);
    }
}
