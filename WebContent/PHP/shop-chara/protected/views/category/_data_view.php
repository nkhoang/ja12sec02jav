<div class="category_block">
    <div class="controller">
        <a title="Edit <?php echo CHtml::encode($data->title); ?>" class="showCategoryForm" href="<?php echo CController::createUrl('/category/ajaxUpdateCategory', array('id' => $data->id,)); ?>">
            <img src="<?php echo Yii::app()->request->baseUrl . '/images/edit.png'; ?>" width="24" height="24" />
        </a>
        <a href="#" title="Delete <?php echo CHtml::encode($data->title); ?>" onclick="var answer = confirm('Are you sure you want to delete ' + '<?php echo CHtml::encode($data->title);?>');
                if (answer) {
                    $.ajax({
                        'url': '<?php echo CController::createUrl('/category/deleteCategory', array(
                            'category_id' => $data->id,
                        ));?>',
                        'type': 'post',
                        'success': function(html){
                            alert('Your request is completed.');
                            $.ajax({
                                'url': category_paging_url,
                                'type': 'post',
                                'success': function(html) {
                                    $('#admin_board').html(html);
                                }
                            });
                        },
                        'error': function(){
                            alert('Failed to complete your request. Please try again later');
                        }
                    });
                }; return false;" >
            <img src="<?php echo Yii::app()->request->baseUrl . '/images/delete_item.png'; ?>" width="24" height="24" />
        </a>
    </div>

    <a class="category-<?php echo CHtml::encode($data->id); ?>" href="<?php echo CController::createUrl('/shop/listItems', array('category_id' => $data->id));?>"
     title="Show <?php echo CHtml::encode($data->title); ?> items"><?php echo CHtml::encode($data->title); ?></a>
</div>