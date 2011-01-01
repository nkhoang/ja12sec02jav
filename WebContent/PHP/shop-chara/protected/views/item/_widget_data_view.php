<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/item/item.css" />
<div id="it_c">
    <div class="img_c">
        <!-- may check image existence here -->
        <img height="80"  src="<?php echo $itemThumbnailLink ?>"/>
    </div>

    <div class="form_c">

        <?php
        $this->renderPartial('/item/_edit_form', array(
            'model' => $model,
            'itemID' => $itemID,
            'categories' => $categories,
            'prefix' => $prefix,
            'performAction' => 'ajaxUpdate',
        ));
        ?>
    </div>
    <div id="item_picture_controller">
        <a title="Add Item Picture" id="showItemPictureForm" href="<?php echo CController::createUrl('/itemPicture/ajaxCreateItemPicture', array('id' => $itemID,)); ?>"> 
            <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32" />
        </a>
    </div>
    <div id="item_pictures">
        <?php
        $this->widget('zii.widgets.CListView', array(
            'id' => 'item_picture_list_view',
            'dataProvider' => $itemPicturesDataProvider,
            'itemView' => '/itemPicture/_data_view', // refers to the partial view named '_post'
            'template' => '{sorter}{items} <div style="clear:both"></div>{pager}{summary}',
            'summaryText' => 'Total: {count}', // @see CBaseListView::renderSummary(),
            'enableSorting' => true,
            'enablePagination' => true,
            'ajaxUpdate' => array('item_board'),
            'pager' => $itemPicturePager,
            'sortableAttributes' => array(
                'item_id' => 'Item ID',
            ),
        ));
        ?>
    </div>
</div>