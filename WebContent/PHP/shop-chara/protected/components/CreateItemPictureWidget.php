<?php
/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

class CreateItemPictureWidget extends CWidget {
    public $itemID = null;
    private $_item = null;
    public function init() {
        $this->_item = new ItemPicture;
    }

    public function getModel() {
        return $this->_item;
    }

    public function run() {
        $this->render('CreateItemPictureForm');
    }
}
?>
