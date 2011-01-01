<div id="itemForm">

    <?php if (Yii::app()->user->hasFlash('itemCreated')): ?>

        <div class="flash-success">
        <?php echo Yii::app()->user->getFlash('itemCreated'); ?>
    </div>

    <?php endif; ?>
        <div class="form">
        <?php
        $form = $this->beginWidget('CActiveForm', array(
                    'id' => 'item-form',
                    'enableAjaxValidation' => true,
                    'action' => CController::createUrl('/item/ajaxCreateItem'),
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
            echo CHtml::dropDownList('category_dropdown_list', '', $categories);
        ?>
            <div class="row buttons">

                <input type="button" name="save_button" value="Save" onclick="$.ajax(
                    {
                        'type': 'post',
                        'url': '<?php
            echo CController::createUrl('/item/ajaxCreateItem'); ?>',
                    'cache':false,
                    'data'  : jQuery(this).parents('form').serialize(),
                    'success':function(html){
                        jQuery('#itemForm').html(html);
                    },
                    'error' : function(x,e) {
                        jQuery('div.form_c').html(x.responseText);
                    }
                });" />
        </div>
        <?php $this->endWidget(); ?>
    </div>
</div><!-- form -->