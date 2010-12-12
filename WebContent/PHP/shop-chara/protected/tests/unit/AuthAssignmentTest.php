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
class AuthAssignmentTest extends CDbTestCase {

    public $fixtures = array(
        'users' => 'User',
        'authtiems' => ':authitem',
        'authenchildren' => ':authitemchild',
        'authassignments' => ':authassignment',
    );

    public function testGetAuthAssignment() {

        $auth1 = Authassignment::model()->findByPk(array('itemname' => 'admin', 'userid' => '1'));

        $this->assertEquals($auth1->itemname, 'admin');
        
        $count = Authassignment::model()->count(array(
                    'condition' => 'itemname=:itemName AND userid=:userID',
                    'params' => array(
                        ':itemName' => 'admin',
                        ':userID' => '1',
                    ),
                ));
        $this->assertEquals($count, 1);
    }

    public function testAssignment() {
        $auth = Yii::app()->authManager;
        $user = User::model()->findByPk(2);
        $auth->assign('admin', $user->id);

        Yii::app()->user->setId($user->id);
        $this->assertTrue(Yii::app()->user->checkAccess('updateItem'));
    }

}

?>
