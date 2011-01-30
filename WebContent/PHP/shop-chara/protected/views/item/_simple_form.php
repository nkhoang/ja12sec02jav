
<div class="content">
    <script type="text/javascript">
        $(function(){
            $('#itemForm input:first').focus();
            $('#itemForm input').keypress(function(e){
                if(e.which == 13){
                    $('#item_save_button').click();
                }
            });
<?php if (Yii::app()->user->hasFlash('itemSaved')): ?>
            $('.flash-success').fadeOut(1000, function() {
                $.ajax({
                    'url': '<?php echo CController::createUrl('itemPicture/ajaxCreateItemPicture', array('id' => $model->id))?>',
                    'type': 'post',
                    'success': function(html){
                        $('#fancybox-content').html(html);
                    }
                });
            });
<?php endif; ?>
        });
        </script>
    <div id="itemForm">
        <?php if (Yii::app()->user->hasFlash('itemSaved')): ?>
            <div class="flash-success">
            <?php echo Yii::app()->user->getFlash('itemSaved'); ?>
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
                <?php echo $form->textField($model, 'price', array('size' => 5, 'maxlength' => 3)); ?>
                <?php echo $form->error($model, 'price'); ?>
            </div>

            <div class="row">
                <?php echo $form->labelEx($model, 'quantity'); ?>
                <?php echo $form->textField($model, 'quantity', array('size' => 4, 'maxlength' => 2)); ?>
                <?php echo $form->error($model, 'quantity'); ?>
            </div>

            <div class="row">
                <?php echo $form->labelEx($model, 'weight'); ?>
                <?php echo $form->textField($model, 'weight', array('size' => 6, 'maxlength' => 4)); ?>
                <?php echo $form->error($model, 'weight'); ?>
            </div>

            <div class="row">
                <?php echo $form->labelEx($model, 'size'); ?>
                <?php echo $form->textField($model, 'size', array('size' => 20)); ?>
                <?php echo $form->error($model, 'size'); ?>
            </div>
                <div class="row">
                <?php echo $form->labelEx($model, 'material'); ?>
                <?php echo $form->textField($model, 'material', array('size' => 20)); ?>
                <?php echo $form->error($model, 'material'); ?>
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
                echo $form->dropDownList($model, 'category_id', $categories);
            ?>
                <div class="row buttons">

                    <input type="button" id="item_save_button" name="save_button" value="Save" onclick="$.ajax(
                        {
                            'type': 'post',
                            'url': '<?php echo CController::createUrl('/item/ajaxCreateItem'); ?>',
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
</div>