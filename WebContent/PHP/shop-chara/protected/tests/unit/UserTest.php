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

}
?>
