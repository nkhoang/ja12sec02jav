<?php
$cs = Yii::app()->clientScript;
$cs->registerScriptFile(Yii::app()->baseUrl . '/js/jquery.autocomplete.js', CClientScript::POS_HEAD);
?>
<script src="<?php echo Yii::app()->request->baseUrl; ?>/js/item/item.manager.js" type="text/javascript"></script>
<script src="<?php echo Yii::app()->request->baseUrl; ?>/js/item/item.scripts.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/jquery.autocomplete.css" />
<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/item/item.css" />
<div id="imageScript"></div>
<div id="itemPictureWidgetForm">
    <div class="form">
        <?php
        $model = $this->getModel();
        $form = $this->beginWidget('CActiveForm', array(
                    'id' => 'item-picture-form',
                    'enableAjaxValidation' => true,
                    'action' => CController::createUrl('/itemPicture/create'),
                ));
        ?>
        You're adding item picture for Item <b>id: <?php echo $itemID ?> </b>

        <br />
        Please input Picasa account information here:

        <label>Picasa Account Name:</label>
        <input type="text" id="picasa_account_name" />

        <label>Album Name:</label>
        <input type="text" id="picasa_album_name" />

        <input type="button" id="picasa_account_update" onclick="updatePicasaAccount('#picasa_account_name','#picasa_album_name');" value="Update"/>


        <br />

        <p class="note">Fields with <span class="required">*</span> are required.</p>

        <?php echo $form->errorSummary($model); ?>

        <div class="row">
            <?php echo $form->labelEx($model, 'title'); ?>
            <?php echo $form->textField($model, 'title', array('maxlength' => 256)); ?>
            <?php echo $form->error($model, 'title'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'description'); ?>
            <?php echo $form->textArea($model, 'description', array('rows' => 3, 'cols' => 30)); ?>
            <?php echo $form->error($model, 'description'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'link'); ?>
            <?php echo $form->textField($model, 'link', array('size' => 60, 'maxlength' => 256, 'id' => 'item_picture_link')); ?>
            <div class="placeholder"></div>
            <?php echo $form->error($model, 'link'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'internal_link'); ?>
            <?php echo $form->textField($model, 'internal_link', array('size' => 60, 'maxlength' => 256)); ?>
            <?php echo $form->error($model, 'internal_link'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'is_thumbnail_picture'); ?>
            <?php echo CHtml::activeCheckBox($model, 'is_thumbnail_picture'); ?>
        </div>

        <?php echo CHtml::hiddenField('itemID', $itemID); ?> <!-- hidden field for storing item id -->

            <div class="row buttons">
            <?php
            echo CHtml::ajaxButton('Next'
                    , CController::createUrl('/itemPicture/ajaxCreateItemPicture'),
                    array(
                        'type' => 'POST',
                        'id' => 'item_picture_submit_button',
                        'success' => 'function(html) {$("#itemPictureWidgetForm").html(html)}'
            )); ?>
        </div>

        <?php $this->endWidget(); ?>
    </div>
</div><!-- form -->