<a href="#" onclick="$.ajax({
            'type': 'post',
            'url': '<?php echo CController::createUrl('/shop/showItems', array('category_id' => $data->id)); ?>',
            'success': function(html) {
                $('#itemsContainer').html(html);
                preloadAllImage();
            }
        });                 
        updateTabSelection(this);
        return false; ">
    <div class="tab <?php echo strtolower(CHtml::encode($data->title)) ?> <?php if ($index === 0)
    echo first; ?>">
        <div class="snt">
        </div>
        <div class="snm">
            <div class="snl">
            </div>
            <div class="snc">
            </div>
            <div class="snr">
            </div>
        </div>
        <div class="snb">
        </div>
    </div>
</a>