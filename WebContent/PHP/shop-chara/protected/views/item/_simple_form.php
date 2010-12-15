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
        <?php echo $form->textField($model, 'item_id', array('size' => 60, 'maxlength' => 256)); ?>
        <?php echo $form->error($model, 'item_id'); ?>
    </div>

    <div class="row">
        <?php echo $form->labelEx($model, 'description'); ?>
        <?php echo $form->textArea($model, 'description', array('rows' => 6, 'cols' => 50)); ?>
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
        <?php echo $form->textField($model, 'is_hot'); ?>
        <?php echo $form->error($model, 'is_hot'); ?>
    </div>

    <div class="row">
        <?php echo $form->labelEx($model, 'is_discounting'); ?>
        <?php echo $form->textField($model, 'is_discounting'); ?>
        <?php echo $form->error($model, 'is_discounting'); ?>
    </div>    

    <div class="row buttons">
        <?php
        echo CHtml::ajaxButton($model->isNewRecord ? 'Create' : 'Save', CController::createUrl('/shop/ajaxCreate'),
                array(
                    'type' => 'POST',
                    'id' => 'item_submit_button',
                    'success' => 'function(html) {$("#itemForm").html(html)}'

        ));
        ?>
    </div>
<?php $this->endWidget(); ?>


</div><!-- form -->