<?php
$cs = Yii::app()->clientScript;
$cs->registerScriptFile(Yii::app()->baseUrl . '/js/jquery.autocomplete.js', CClientScript::POS_HEAD);
?>
<script src="<?php echo Yii::app()->request->baseUrl; ?>/js/item/item.manager.js" type="text/javascript"></script>

<script src="<?php echo Yii::app()->request->baseUrl; ?>/js/item/item.scripts.js" type="text/javascript"></script>
<link rel="stylesheet" type="text/css" href="<?php echo Yii::app()->request->baseUrl; ?>/css/jquery.autocomplete.css" />

<script type="text/javascript">
    $(function(){
        loadItemsData();
    });
</script>
<style type="text/css">
    img.btn {
        cursor: pointer;
        vertical-align: middle;
    }

    #refreshItems{
    	position: absolute;
    	right: 0;
    	top: 0;
    }

    #itemForm dt {
    	width: 120px;
    }
    #itemForm dd {
    	width: 390px;
    }
    /* Thumbnail container */
    .thumbnailContainer {
        height: 40px;
        width: 40px;
        padding: 2px;
        border: 1px solid #9F9F9F;
        float: left;
        margin: 3px;
        cursor: pointer;
    }

	.placeholder .thumbnailContainer {
		float: none;
	}

    .thumbnailContent {
        height: 100%;
        width: 100%;
        background-color: #FFF;
        text-align: center;
    }

    .thumbnailContent img {
        vertical-align: middle;
    }

    .thumbnailContent.loading {
        background: #FFF url(/images/simple/loading.gif) center center no-repeat;
    }

    #thumbnailArea {
        width: 200px;
    }
</style>
<div id="imageScript"></div>
<div id="itemForm">
    <div class="form">
        <?php
        $form = $this->beginWidget('CActiveForm', array(
                    'id' => 'item-form',
                    'enableAjaxValidation' => true,
                ));
        ?>

        <p class="note">Fields with <span class="required">*</span> are required.</p>

        <?php echo $form->errorSummary($model); ?>

        <div class="row">
            <?php echo $form->labelEx($model, 'item_id'); ?>
            <?php echo $form->textField($model, 'item_id', array('maxlength' => 256)); ?>
            <div class="placeholder"></div>
            <?php echo $form->error($model, 'item_id'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'description'); ?>
            <?php echo $form->textArea($model, 'description', array('rows' => 2, 'cols' => 30)); ?>
            <?php echo $form->error($model, 'description'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'price'); ?>
            <?php echo $form->textField($model, 'price', array('size' => 20, 'maxlength' => 20)); ?>
            <?php echo $form->error($model, 'price'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'quantity'); ?>
            <?php echo $form->textField($model, 'quantity'); ?>
            <?php echo $form->error($model, 'quantity'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'is_hot'); ?>
            <?php echo CHtml::activeCheckBox($model, 'is_hot') ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'is_discounting'); ?>
            <?php
            echo CHtml::activeCheckBox($model, 'is_discounting');
            ?>
        </div>

        <div class="row buttons">
            <?php
            echo CHtml::ajaxButton($model->isNewRecord ? 'Create' : 'Save', CController::createUrl('/shop/ajaxCreateItem'),
                    array(
                        'type' => 'POST',
                        'id' => 'item_submit_button',
                        'success' => 'function(html) {$("#itemForm").html(html);}',
            ));
            ?>
        </div>
        <?php $this->endWidget(); ?>
    </div>
</div><!-- form -->