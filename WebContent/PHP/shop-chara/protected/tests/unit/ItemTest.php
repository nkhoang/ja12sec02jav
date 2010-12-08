<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

class ItemTest extends CDbTestCase {

    public $fixtures = array(
        'items' => 'Item',
        'itemPictures' => 'ItemPicture',
    );

    public function testSimpleSearch() {
        Yii::trace('Test simple search.');
        $item = Item::model()->findByPk(1);
        $this->assertEquals($item->quantity, 10);
    }

    public function testUsingCount() {
        $count = Item::model()->count(array(
                    'select' => '*'
                )); // using CDbCriterial as an array

        $this->assertEquals($count, 3); // assert this is true = 3.

        $count = Item::model()->count(array(
                    'select' => '*',
                    'condition' => 'item_id=:itemID',
                    'params' => array(':itemID' => 'IT0002'),
                ));

        $this->assertEquals($count, 1);
    }

    public function testUsingExist() {
        $item_exist = Item::model()->exists(array(
                    'select' => '*',
                    'condition' => 'item_id=:itemID',
                    'params' => array(':itemID' => 'IT0002'),
                ));
        $this->assertTrue($item_exist);
    }

    public function testUsingTransaction() {
        $connection = Yii::app()->db;
        $item = Item::model()->findByPk(1);

        $this->assertTrue(isset($item));
        $transaction = $connection->beginTransaction();
        try {
            $item->item_id = 'IT0004';

            $item->save();
            $transaction->commit();
        } catch (Exception $ex) {

        }

        // test update
        $item = Item::model()->findByPk(1);
        $this->assertEquals($item->item_id, 'IT0004');
    }


    public function testUsingTransactionWithAR() {
        $transaction = Item::model()->dbConnection->beginTransaction();
        try {
            $item = Item::model()->findByPk(1);
            $item->item_id = 'IT0005';
            $item->save();
            $transaction->commit();
        } catch (Exception $e) {
            $transaction->rollBack();
        }
    }

}

?>
