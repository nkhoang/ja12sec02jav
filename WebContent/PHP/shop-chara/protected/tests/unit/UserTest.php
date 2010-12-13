<?php
/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of UserTest
 *
 * @author hoangnk
 */
class UserTest extends CDbTestCase{

    public $fixtures = array(
        'users' => 'User',
        'authtiems' => ':authitem',
        'authenchildren' => ':authitemchild',
        'authassignments' => ':authassignment',
    );

    public function testGetUser() {
        $user = User::model()->findByPk(1);
        $this->assertEquals($user->id, 1);
    }

    public function testGetRoles() {
        $userRoles = Authassignment::model()->findAll(array(
            'condition' => 'userid=:userID',
            'params' => array(':userID'=> '1'),
        ));

        echo 'Number of user roles: '.sizeof($userRoles);

        $userRoles = CHtml::listData($userRoles, 'itemname', 'userid');
        echo '\n Role list: ';
        foreach($userRoles as $rolename => $userID) {
            echo '\nRole: '.$rolename.' User: '.$userID;
        }
        $this->assertEquals(1, sizeof($userRoles));
    }

}
?>
