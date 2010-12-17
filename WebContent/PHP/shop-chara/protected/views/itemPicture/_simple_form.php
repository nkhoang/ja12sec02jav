<script type="text/javascript">
    // resize fancybox
    $(function(){        
        $('#fancybox-inner').width(400);
        $('#fancybox-wrap').width(420);
        $('#fancybox-content').width(400);
        $.fancybox.center();
        
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

<div id="itemPictureForm">
    <div class="form">
        <?php
        $cs = Yii::app()->clientScript;
        $cs->registerScriptFile(Yii::app()->baseUrl . '/js/jquery.autocomplete.js', CClientScript::POS_HEAD);
        $cs->registerScriptFile(Yii::app()->baseUrl . '/js/item/item.scripts.js', CClientScript::POS_HEAD);
        $cs->registerCssFile(Yii::app()->baseUrl . '/css/jquery.autocomplete.css', CClientScript::POS_HEAD);                        
        ?>
        
        <?php
        $form = $this->beginWidget('CActiveForm', array(
                    'id' => 'item-picture-form',
                    'enableAjaxValidation' => true,
                ));
        ?>
        You're adding item picture for Item <b>id: <?php echo $itemID ?> </b>
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
                    , CController::createUrl('/shop/ajaxCreateItemPicture'),
                    array(
                        'type' => 'POST',
                        'id' => 'item_picture_submit_button',
                        'success' => 'function(html) {$("#itemForm").html(html)}'
            )); ?>
        </div>

        <?php $this->endWidget(); ?>
    </div>
</div><!-- form -->