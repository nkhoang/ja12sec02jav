<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of IssueTest
 *
 * @author hnguyen93
 */
class IssueTest extends CDbTestCase {

    /**
     * Test get type options of Issue object.
     */
    public function testGetTypeOptions() {
        $options = Issue::model()->typeOptions;
        $this->assertTrue(is_array($options));
        $this->assertTrue(3 == count($options));
        $this->assertTrue(in_array('Bug', $options));
        $this->assertTrue(in_array('Feature', $options));
        $this->assertTrue(in_array('Task', $options));
    }

    public function testGetStatusOptions() {
        $options = Issue::model()->statusOptions;
        $this->assertTrue(is_array($options));
        $this->assertTrue(3 == count($options));
        $this->assertTrue(in_array('Not yet started', $options));
        $this->assertTrue(in_array('Started', $options));
        $this->assertTrue(in_array('Finished', $options));
    }

}

?>
