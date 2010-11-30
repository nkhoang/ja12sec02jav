<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ProjectTest
 *
 * @author hoangnk
 */
class ProjectTest extends CDbTestCase {

    public $fixtures = array(
        'projects' => 'Project',
        'users' => 'User', // this is a AR class.
        'ProjUsrAssign' => ':project_user_assignment', // ':' indicates that this is a database table not AR class.
    );

    public function testGetUserOptions() {
        $project = $this->projects('project1');
        $options = $project->userOptions;

        $this->assertTrue(is_array($options));
        $this->assertTrue(count($options) > 0);
    }

    public function testCRUD() {
        $newProject = new Project();
        $newProjectName = 'Test Project 1';
        $newProject->setAttributes(array(
            'name' => $newProjectName,
            'description' => 'Test project number one',
            'create_time' => '2010 01-01 00:00:00',
            'create_user_id' => 1,
            'update_time' => '2010-01-01 00:00:00',
            'update_user_id' => 1,
        ));
        $this->assertTrue($newProject->save(false));

        //Create a new project
//READ back the newly created project
        $retrievedProject = Project::model()->findByPk($newProject->id);
        $this->assertTrue($retrievedProject instanceof Project);
        $this->assertEquals($newProjectName, $retrievedProject->name);

        //UPDATE the newly created project
        $updatedProjectName = 'Updated Test Project 1';
        $newProject->name = $updatedProjectName;
        $this->assertTrue($newProject->save(false));
//read back the record again to ensure the update worked
        $updatedProject = Project::model()->findByPk($newProject->id);
        $this->assertTrue($updatedProject instanceof Project);
        $this->assertEquals($updatedProjectName, $updatedProject->name);
//DELETE the project
        $newProjectId = $newProject->id;
        $this->assertTrue($newProject->delete());
        $deletedProject = Project::model()->findByPk($newProjectId);
        $this->assertEquals(NULL, $deletedProject);
    }

    public function testRead() {
        $retrievedProject = $this->projects('project1');
        $this->assertTrue($retrievedProject instanceof Project);
        $this->assertEquals('Test Project 1', $retrievedProject->name);
    }

}

?>
