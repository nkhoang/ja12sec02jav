<div id="categoryForm">
    <?php if (Yii::app()->user->hasFlash('categoryUpdated')): ?>

        <div class="flash-success">
        <?php echo Yii::app()->user->getFlash('categoryUpdated'); ?>
    </div>

    <?php endif; ?>
    
        <div class="form">

        <?php
        $form = $this->beginWidget('CActiveForm', array(
                    'id' => 'category-form',
                    'enableAjaxValidation' => false,
                ));
        ?>

        <p class="note">Fields with <span class="required">*</span> are required.</p>

        <?php echo $form->errorSummary($model); ?>

        <div class="row">
            <?php echo $form->labelEx($model, 'title'); ?>
            <?php echo $form->textField($model, 'title'); ?>
            <?php echo $form->error($model, 'title'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'category_code'); ?>
            <?php echo $form->textField($model, 'category_code'); ?>
            <?php echo $form->error($model, 'category_code'); ?>
        </div>

        <div class="row">
            <?php echo $form->labelEx($model, 'description'); ?>
            <?php echo $form->textField($model, 'description'); ?>
            <?php echo $form->error($model, 'description'); ?>
        </div>


        <div class="row buttons">
            <input type="button" name="save_button" value="Save" onclick="$.ajax(
                {
                    'type': 'post',
                    'url': '<?php
            echo CController::createUrl('/category/ajaxUpdateCategory', array(
                'id' => $model->id,
            )); ?>',
                    'cache':false,
                    'data'  : jQuery(this).parents('form').serialize(),
                    'success':function(html){
                        jQuery('#categoryForm').html(html);
                    },
                    'error' : function(x,e) {
                        jQuery('#categoryForm').html(x.responseText);
                    }
                });" />
        </div>

        <?php $this->endWidget(); ?>

    </div><!-- form -->
</div>