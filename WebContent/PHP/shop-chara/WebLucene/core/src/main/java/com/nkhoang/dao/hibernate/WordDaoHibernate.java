package com.nkhoang.dao.hibernate;

import com.nkhoang.dao.WordDao;
import com.nkhoang.model.Word;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: 1/23/11
 * Time: 10:47 AM
 * To change this template use File | Settings | File Templates.
 */

@Repository("wordDao")
public class WordDaoHibernate extends GenericDaoHibernate<Word, Long> implements WordDao {
    public WordDaoHibernate() {
        super(Word.class);
    }

    public boolean find(String w) {
        boolean result = false;

        List<Word> words = getHibernateTemplate().find("from Word where description=?", w);
        if (words.size() > 0) {
            result = true;
        }
        return result;
    }

}
