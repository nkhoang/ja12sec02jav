<div id="itemForm">
    <div class="form">

        <script type="text/javascript">
            // will be used one then will be removed
            function loadJS(href){
                var $script = $('<script>').attr('src', href);
                $('#imageScript').html($script);
            }
            

            var user = 'myhoang0603';
            var albumData = [{
                    album: 'CharaBigThumbnail',
                    renderer: 'renderThumbnailBig'
                }, {
                    album: 'CharaPreview',
                    renderer: 'renderThumbnailPreview'
                }, {
                    album: 'CharaThumbnail',
                    renderer: 'renderThumbnail'
                }];
            var maxres = 1000; // 0 - for all;
            var authkey = '';

            function loadItemsData(){
                for (var i in albumData) {
                    var url = 'http://picasaweb.google.com/data/feed/api/user/' + user + '/album/' + albumData[i].album + '?kind=photo&alt=json-in-script&callback=' + albumData[i].renderer + '&access=public&start-index=1';

                    if (maxres && maxres != 0) {
                        url = url + '&max-results=' + maxres;
                    }
                    if (authkey && authkey != '') {
                        url = url + '&authkey=' + authkey;
                    }
                    loadJS(url);
                }
            }
        </script>
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
            echo CHtml::ajaxButton($model->isNewRecord ? 'Create' : 'Save', CController::createUrl('/shop/ajaxCreate'),
                    array(
                        'type' => 'POST',
                        'id' => 'item_submit_button',
                        'success' => 'function(html) {$("#itemForm").html(html)}'
            ));
            ?>
        </div>
        <?php $this->endWidget(); ?>
    </div>
</div><!-- form -->