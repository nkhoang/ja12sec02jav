CREATE TABLE IF NOT EXISTS project
(
id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(128),
description TEXT,
create_time DATETIME,
create_user_id INTEGER,
update_time DATETIME,
update_user_id INTEGER
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS issue
(
id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
name varchar(256) NOT NULL,
description varchar(2000),
project_id INTEGER,
type_id INTEGER,
status_id INTEGER,
owner_id INTEGER,
requester_id INTEGER,
create_time DATETIME,
create_user_id INTEGER,
update_time DATETIME,
update_user_id INTEGER
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS user
(
id INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
email Varchar(256) NOT NULL,
username Varchar(256),
password Varchar(256),
last_login_time Datetime,
create_time DATETIME,
create_user_id INTEGER,
update_time DATETIME,
update_user_id INTEGER
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS project_user_assignment
(
project_id Int(11) NOT NULL,
user_id Int(11) NOT NULL,
create_time DATETIME,
create_user_id INTEGER,
update_time DATETIME,
update_user_id INTEGER,
PRIMARY KEY (project_id,user_id)
) ENGINE = InnoDB;


ALTER TABLE issue ADD CONSTRAINT FK_issue_project FOREIGN KEY (project_id)
REFERENCES trackstar_dev.project(id)
ON DELETE CASCADE ON
UPDATE RESTRICT;

ALTER TABLE issue ADD CONSTRAINT FK_issue_owner FOREIGN KEY
(owner_id) REFERENCES trackstar_dev.user (id) ON DELETE CASCADE ON UPDATE
RESTRICT;

ALTER TABLE issue ADD CONSTRAINT FK_issue_requester FOREIGN
KEY (requester_id) REFERENCES trackstar_dev.user (id) ON DELETE CASCADE ON
UPDATE RESTRICT;

ALTER TABLE project_user_assignment ADD CONSTRAINT FK_project_user FOREIGN KEY (project_id) REFERENCES trackstar_dev.project (id) ON
DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE project_user_assignment ADD CONSTRAINT FK_user_project FOREIGN KEY (user_id) REFERENCES trackstar_dev.user (id) ON
DELETE CASCADE ON UPDATE RESTRICT;

INSERT INTO user (email, username, password)
VALUES ('test1@notanaddress.com','Test_User_One', MD5('test1')), ('test2@notanaddress.com','Test_User_Two', MD5('test2'));

INSERT INTO project (name, description) VALUES ('Project 1', 'Project 1 Description');

INSERT INTO project_user_assignment (project_id, user_id ) VALUES (1,1), (1,2);