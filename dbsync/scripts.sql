/* WORD_UPDATE_CREATION_DATE */
DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    TRIGGER WORD_UPDATE_CREATION_DATE BEFORE INSERT
    ON WORD
    FOR EACH ROW BEGIN
        SET NEW.creationDate = NOW();
    END$$

DELIMITER ;

/* WORD_UPDATE_MOD_DATE */

DELIMITER $$

CREATE
    /*[DEFINER = { user | CURRENT_USER }]*/
    TRIGGER dbsync.WORD_UPDATE_MOD_DATE BEFORE UPDATE
    ON dbsync.WORD
    FOR EACH ROW BEGIN
	SET NEW.modificationDate = NOW();
    END$$

DELIMITER ;