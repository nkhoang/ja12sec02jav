package com.nkhoang.dao;

import com.nkhoang.model.Role;
import com.nkhoang.model.Word;

/**
 * Word Data Access Object (DAO) interface.
 *
 * @author <a href="mailto:nkhoang.it@gmail.com">HNK</a>
 */
public interface WordDao extends GenericDao<Word, Long> {
    // there is nothing to be defined.

    boolean find(String w);

}
