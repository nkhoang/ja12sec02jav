<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/item/item.css" />

<div id="it_c">
    <div class="img_c">
        <!-- may check image existence here -->
        <img height="80"  src="<?php echo $itemThumbnailLink ?>"/>
    </div>

    <div class="form_c">
        <?php $this->renderPartial('/item/_simple_form', array(
            'model' => $model,
            'itemID' => $itemID
        )); ?>
    </div>
</div>