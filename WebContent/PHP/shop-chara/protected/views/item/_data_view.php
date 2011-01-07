<div class="ic" onclick="$.ajax(
    {
        'type': 'post',
        'data':{
            'item_id':'<?php echo $data->id; ?>'
        },
        'url': '<?php echo CController::createUrl('/shop/viewItemDetails'); ?>',
        'cache':false,
        'success':function(html){
            jQuery('#item_board').html(html);
        },
        'error' : function(x,e) {
            jQuery('#item_board').html(x.responseText);
        }
    });">
    <div class="it_b">
        <div class="wraptocenter">
            <label class="label_radio" for="sample-radio">
                <input name="sample-radio" id="sample-radio" value="1" type="radio" />
            </label>
        </div>
    </div>

    <div class="img_c it_b">


        <!-- may check image existence here -->
        <?php if (sizeof($data->itemPictures) === 0): ?>
            <div class="wraptocenter">
                <img height="60"  src="<?php echo Yii::app()->request->baseUrl . '/images/photo_not_available.jpg' ?>"/>
            </div>
        <?php else: ?>
                <div class="wraptocenter">
                    <img height="60"  src="<?php echo $data->itemPictures[0]->link ?>"/>
                </div>
        <?php endif; ?>
            </div>

            <div class="it_b">
                <div class="it_title"><?php echo CHtml::encode($data->getAttributeLabel('item_id')); ?></div>
                <div class="seperator"></div>
                <div class="it_content"><?php echo CHtml::encode($data->item_id); ?></div>
            </div>

            <div class="it_b">
                <div class="it_title"><?php echo CHtml::encode($data->getAttributeLabel('price')); ?></div>
                <div class="seperator"></div>
                <div class="it_content"><?php echo CHtml::encode($data->price); ?></div>
            </div>

            <div class="it_b">
                <div class="it_title"><?php echo CHtml::encode($data->getAttributeLabel('quantity')); ?></div>
                <div class="seperator"></div>
                <div class="it_content"><?php echo CHtml::encode($data->quantity); ?></div>
            </div>
            <div class="it_b">
                <div class="it_title"><?php echo CHtml::encode($data->getAttributeLabel('weight')); ?></div>
                <div class="seperator"></div>
                <div class="it_content"><?php echo CHtml::encode($data->weight); ?></div>
            </div>        
            <div class="it_b">
                <div class="it_title"><?php echo CHtml::encode($data->getAttributeLabel('is_hot')); ?></div>
                <div class="seperator"></div>
                <div class="it_content">
                    <div class="wraptocenter">
                        <span></span>
                        <img width="16" height="16" src="
                <?php
                if ($data->is_hot === '0') {
                    echo Yii::app()->request->baseUrl . '/images/off.png';
                } else {
                    echo Yii::app()->request->baseUrl . '/images/on.png';
                }
                ?>" />
            </div></div>
    </div>

    <div class="it_b">
        <div class="it_title"><?php echo CHtml::encode($data->getAttributeLabel('is_discounting')); ?></div>
        <div class="seperator"></div>
        <div class="it_content">
            <div class="wraptocenter">
                <span></span>
                <img width="16" height="16" src="
                <?php
                     if ($data->is_discounting === '0') {
                         echo Yii::app()->request->baseUrl . '/images/off.png';
                     } else {
                         echo Yii::app()->request->baseUrl . '/images/on.png';
                     }
                ?>" />
            </div>
        </div>
    </div>
    <!--
    <div class="it_b">
        <div class="it_title"><?php echo CHtml::encode($data->getAttributeLabel('category_id')); ?></div>
        <div class="seperator"></div>
        <div class="it_content"><?php echo CHtml::encode($data->category_id); ?></div>
</div>
    -->
</div>

