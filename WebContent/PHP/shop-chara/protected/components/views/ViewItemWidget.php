<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/widget/viewItemWidget.css" media="screen, projection" />

<div id="it_c">
    <div class="img_c">
        <!-- may check image existence here -->
        <img height="80"  src="<?php echo $this->getThumbnail(); ?>"/>
    </div>

    <div class="form_c">
        <?php Yii::app()->runController('/item/ajaxCreateItem'); ?> <!-- use ItemController to render form view.-->
    </div>
</div>