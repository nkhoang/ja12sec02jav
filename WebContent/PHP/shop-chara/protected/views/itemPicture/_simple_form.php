<script type="text/javascript">
    $(function(){
        alert('aaa');
        $('#itemPictureForm').bind('resize',function(){        
            height = $('#itemPictureForm').width()+50;
            $('#fancybox-wrap').width(height);
           
        });
         });
    
</script>

<div id="itemPictureForm">
    <div class="form">
        <?php
        $cs = Yii::app()->clientScript;
        $cs->registerScriptFile(Yii::app()->baseUrl . '/js/jquery.autocomplete.js', CClientScript::POS_HEAD);
        $cs->registerCssFile(Yii::app()->baseUrl . '/css/jquery.autocomplete.css', CClientScript::POS_HEAD);
        ?>
        <script type="text/javascript">

            var MAX_ITEM_ALLOW = 10;
            var subPictureIdArr = new Array();
            var itemArr = new Array();
            var itemCounter = 0, idNumber = 0;

            function renderThumbnail(data){
                var album = data.feed.gphoto$name.$t;
                itemArr[album] = new Array(data.feed.entry.length);
                for (var i = 0; i < data.feed.entry.length; i++) {
                    addDiv(data.feed.entry[i], i, album);
                }

                $('#itemThumbnail').autocomplete(itemArr[album], {
                    width: 310,
                    minChars: 0,
                    max: 1000,
                    scrollHeight: 300,
                    matchContains: true,
                    formatItem: function(data, i, n, value){
                        return "<table><tr><td>" + data.picSmall + "</td><td>" + data.description + "-" + data.title + "</td></tr></table>";
                    },
                    formatMatch: function(row, i, max){
                        return row.description + ' ' + row.title;
                    },
                    formatResult: function(row){
                        return row.org;
                    },
                    onEnter: function(inputVal){
                        var $this = $('#itemThumbnail');
                        var html = buildThumbnail(inputVal, false);
                        $this.parents('dd').find('.placeholder').html(html);
                    }
                });
            }

            function renderThumbnailBig(data){
                var album = data.feed.gphoto$name.$t;
                itemArr[album] = new Array(data.feed.entry.length);
                for (var i = 0; i < data.feed.entry.length; i++) {
                    addDiv(data.feed.entry[i], i, album);
                }
                $('#itemThumbnailBig').autocomplete(itemArr[album], {
                    autocomplete: true,
                    width: 310,
                    minChars: 0,
                    max: 1000,
                    scrollHeight: 300,
                    matchContains: true,
                    formatItem: function(data, i, n, value){
                        return "<table><tr><td>" + data.picSmall + "</td><td>" + data.description + "-" + data.title + "</td></tr></table>";
                    },
                    formatMatch: function(row, i, max){
                        return row.description + ' ' + row.title;
                    },
                    formatResult: function(row){
                        return row.org;
                    },
                    onEnter: function(inputVal){
                        var $this = $('#itemThumbnailBig');
                        var html = buildThumbnail(inputVal, false);
                        $this.parents('dd').find('.placeholder').html(html);
                    }
                });
            }

            function renderThumbnailPreview(data){
                var album = data.feed.gphoto$name.$t;
                itemArr[album] = new Array(data.feed.entry.length);
                for (var i = 0; i < data.feed.entry.length; i++) {
                    addDiv(data.feed.entry[i], i, album);
                }
                $('.itemSubPic').autocomplete(itemArr[album], {
                    width: 310,
                    minChars: 0,
                    max: 1000,
                    scrollHeight: 300,
                    matchContains: true,
                    formatItem: function(data, i, n, value){
                        return "<table><tr><td>" + data.picSmall + "</td><td>" + data.description + "-" + data.title + "</td></tr></table>";
                    },
                    formatMatch: function(row, i, max){
                        return row.description + ' ' + row.title;
                    },
                    formatResult: function(row){
                        return row.org;
                    },
                    onEnter: function(inputVal, $ele){
                        if (itemCounter == MAX_ITEM_ALLOW) {
                            return;
                        }
                        $ele.val('');
                        var html = buildThumbnail(inputVal);
                        $('#thumbnailArea').prepend(html);
                    }
                });
            }


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
            <?php echo $form->textField($model, 'link', array('size' => 60, 'maxlength' => 256)); ?>
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

        <div class="row buttons">
            <?php
            echo CHtml::ajaxButton($model->isNewRecord ? 'Create' : 'Save'
                    , CController::createUrl('/shop/ajaxCreateItemPicture'),
                    array(
                        'type' => 'POST',
                        'id' => 'item_picture_submit_button',
                        'success' => 'function(html) {$("#itemPictureForm").html(html)}'
            )); ?>
        </div>

        <?php $this->endWidget(); ?>
    </div>
</div><!-- form -->