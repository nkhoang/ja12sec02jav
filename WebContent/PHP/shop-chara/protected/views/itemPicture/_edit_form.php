<script type="text/javascript">
    // resize fancybox
    $(function(){
        $.fancybox.resize();
        loadAlbum('myhoang0603', 'CharaBigThumbnail', 'renderThumbnail');

        // build preview thumbnail
        var $this = $('#item_picture_link');
        var inputVal = $this.val();
        if (inputVal.length > 0) {
            var html = buildPreviewThumbnail(inputVal);
            $this.parents('div.row').find('.placeholder').html(html);
        }
    });    
</script>
<div id="imageScript"></div>
<div id="itemPictureEditForm">
    <div class="form">

        <?php if (Yii::app()->user->hasFlash('itemPictureUpdated')): ?>

            <div class="flash-success">
            <?php echo Yii::app()->user->getFlash('itemPictureUpdated'); ?>
        </div>

        <?php endif; ?>

        <?php
            $form = $this->beginWidget('CActiveForm', array(
                        'id' => 'item-picture-edit-form',
                        'enableAjaxValidation' => true,
                        'action' => CController::createUrl('/itemPicture/create'),
                    ));
        ?>
            You're adding item picture for Item <b>id: <?php echo $itemID ?> </b>

            <br />
            Please input Picasa account information here:

            <label>Picasa Account Name:</label>
            <input type="text" id="picasa_account_name" value="myhoang0603"/>

            <label>Album Name:</label>
            <input type="text" id="picasa_album_name" value="CharaBigThumbnail"/>

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

                <input type="button" name="save_button" value="Save" onclick="$.ajax(
                    {
                        'type': 'post',
                        'url': '<?php
            echo CController::createUrl('/itemPicture/ajaxUpdateItemPicture', array(
                'id' => $model->id,
            )); ?>',
                    'cache':false,
                    'data'  : jQuery(this).parents('form').serialize(),
                    'success':function(html){
                        jQuery('#itemPictureEditForm').html(html);
                    },
                    'error' : function(x,e) {
                        jQuery('#itemPictureEditForm').html(x.responseText);
                    }
                });" />
        </div>

        <?php $this->endWidget(); ?>
    </div>
</div><!-- form -->