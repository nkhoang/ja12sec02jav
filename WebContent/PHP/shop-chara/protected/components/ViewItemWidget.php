<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

class ViewItemWidget extends CWidget {

    public $item_id = null;
    private $_item = null;

    public function init() {
        if ($this->item_id === null) {
            throw new CHttpException(403, Yii::t('yii', 'Missing argument: itemID to render widget.'));
        } else {
            $this->_item = Item::model()->with('itemPictures')->findByPk((int) $this->item_id);
        }
    }

    public function getModel() {
        return $this->_item;
    }

    public function getThumbnail() {
        Yii::log('getThumbnail'.count($itemThumbnail), 'info', 'debug');
        
        $itemThumbnail = ItemPicture::model()->find(array( // find just one
                    'condition' => 'item_id=:itemID AND is_thumbnail_picture=:isThumbnail',
                    'params' => array(
                        ':itemID' => $this->item_id,
                        ':isThumbnail' => 1,
                    ),
                ));

        Yii::log('getThumbnail'.count($itemThumbnail), 'info', 'debug');

        if (count($itemThumbnail) > 0) {
            return $itemThumbnail->link;
        }
    }

    public function run() {
        $this->render('ViewItemWidget');
    }

}

?>
