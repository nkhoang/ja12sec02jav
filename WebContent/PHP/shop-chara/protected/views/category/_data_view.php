<div class="category_block">
    <div class="controller">
        <a title="Edit <?php echo CHtml::encode($data->title); ?>" class="showCategoryForm" href="<?php echo CController::createUrl('/category/ajaxUpdateCategory', array('id' => $data->id,)); ?>">
            <img src="<?php echo Yii::app()->request->baseUrl . '/images/edit.png'; ?>" width="24" height="24" />
        </a>
    </div>
    <a class="category-<?php echo CHtml::encode($data->id); ?>" onclick="$.ajax(
        {
            'type': 'post',
            'data':{
                'category_id':'<?php echo $data->id; ?>'
            },
            'url': '<?php echo CController::createUrl('/shop/listItems'); ?>',
            'cache':false,
            'success':function(html){
                //jQuery('#admin_board').html(html);
                var reply = $(html);
                console.debug(html);
                var target = $('#admin_board');
                target.html('');
                target.append(reply.filter('script[src]').filter(function() { return $.inArray($(this).attr('src'), script_files) === -1; }));
                target.append(reply.filter('link[href]').filter(function() { return $.inArray($(this).attr('href'), css_files) === -1; }));
                target.append(reply.filter('div.content'));
                target.append(reply.filter('script:not([src])'));
            },
            'error' : function(x,e) {
                jQuery('#admin_board').html(x.responseText);
            }
        }); return false;"
       href="#" title="Show <?php echo CHtml::encode($data->title); ?> items"><?php echo CHtml::encode($data->title); ?></a>
</div>