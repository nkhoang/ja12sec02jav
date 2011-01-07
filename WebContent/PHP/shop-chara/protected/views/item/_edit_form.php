<p>You are editing Item id = <b><?php echo $itemID; ?></b></p>

<div id="itemForm">

    <?php if (Yii::app()->user->hasFlash('itemUpdated')): ?>

        <div class="flash-success">
        <?php echo Yii::app()->user->getFlash('itemUpdated'); ?>
    </div>

    <?php endif; ?>

        <div class="form">
        <?php
        $form = $this->beginWidget('CActiveForm', array(
                    'id' => 'edit-item-form',
                    'enableAjaxValidation' => true,
                    'action' => CController::createUrl('/item/' . $performAction, array(
                        'id' => $itemID,
                    )),
                ));
        ?>

        <p class="note">Fields with <span class="required">*</span> are required.</p>

        <?php echo $form->errorSummary($model); ?>

        <div class="row">
            <?php echo $form->labelEx($model, 'item_id'); ?>
            <?php echo $form->dropDownList($model, 'category_prefix', $prefix); ?>
            <?php echo $form->textField($model, 'number_part', array('size' => 5, 'maxlength' => 5)); ?>
            <?php echo $form->error($model, 'number_part'); ?>
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
            <?php echo $form->labelEx($model, 'weight'); ?>
            <?php echo $form->textField($model, 'weight'); ?>
            <?php echo $form->error($model, 'weight'); ?>
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

        <?php echo $form->labelEx($model, 'category_id'); ?>
        <?php
            echo CHtml::dropDownList('category_dropdown_list', $model->category_id, $categories);
        ?>

            <div class="row buttons">
                <input type="button" name="save_button" value="Save" onclick="$.ajax(
                    {
                        'type': 'post',
                        'url': '<?php
            echo CController::createUrl('/item/ajaxUpdate', array(
                'id' => $itemID,
            )); ?>',
                    'cache':false,
                    'data'  : jQuery(this).parents('form').serialize(),
                    'success':function(html){
                        jQuery('div.form_c').html(html);
                    },
                    'error' : function(x,e) {
                        jQuery('div.form_c').html(x.responseText);
                    }
                });" />
            <input type="button" name="back_to_item_list" value="Cancel" onclick="$.ajax(
                {
                    'type': 'post',
                    'data':{
                        'category_id':'<?php echo $model->category_id; ?>'},
                    'url': '<?php echo CController::createUrl('/shop/listItems'); ?>',
                    'cache':false,
                    'success':function(html){
                        jQuery('#item_board').html(html)
                    },
                    'error' : function(x,e) {
                        jQuery('#item_board').html(x.responseText);
                    }
                });" />
        </div>
        <?php $this->endWidget(); ?>
    </div>
</div><!-- form -->