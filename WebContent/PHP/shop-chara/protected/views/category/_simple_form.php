<div class="content">
    <script type="text/javascript">
        $(function(){
            $('#categoryForm input:first').focus();
            $('#categoryForm input').keypress(function(e){
                if(e.which == 13){
                    $('#category_save_btn').click();
                }
            });
<?php if (Yii::app()->user->hasFlash('categoryUpdated')): ?>
            $('.flash-success').fadeOut(2000, function() {
                $.fancybox.close();
            });
<?php endif; ?>
        });
        </script>
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
                        'enableAjaxValidation' => true,
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
                <?php echo $form->textField($model, 'category_code', array('size' => 5, 'maxlength' => 2)); ?>
                <?php echo $form->error($model, 'category_code'); ?>
            </div>

            <div class="row">
                <?php echo $form->labelEx($model, 'description'); ?>
                <?php echo $form->textField($model, 'description'); ?>
                <?php echo $form->error($model, 'description'); ?>
            </div>

            <div class="row buttons">
                <input id="category_save_btn" type="button" name="save_button" value="Save" onclick="$.ajax(
                                {
                                    'type': 'post',
                                    'url': '<?php
                if ($model->id) {
                    echo CController::createUrl('/category/ajaxUpdateCategory', array(
                        'id' => $model->id,
                    ));
                } else {
                    echo CController::createUrl('/category/ajaxUpdateCategory');
                } ?>',
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
</div>