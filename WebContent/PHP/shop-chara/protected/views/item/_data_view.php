<a href="<?php echo CController::createUrl('/shop/viewItemDetails', array('item_id' => $data->id)); ?>">
    <div class="ic <?php if ($index %2 === 0) echo 'even'; else echo 'odd'?>">
        <div class="it_b">
            <div class="wraptocenter">
                <label class="label_checkbox">
                    <input class="<?php echo CHtml::encode($data->id);?>" name="sample-checkbox" type="checkbox"/>
                </label>
            </div>
        </div>

        <div class="img_c it_b">
            <!-- may check image existence here -->
        <?php if (sizeof($data->itemPictures) === 0 || $data->getThumbnailPicture() !== null): ?>
            <div class="wraptocenter">
                <img height="60" src="<?php echo Yii::app()->request->baseUrl . '/images/photo_not_available.jpg' ?>"/>
            </div>
        <?php else: ?>
            <div class="wraptocenter">
                <img height="60" src="<?php echo $data->itemPictures[0]->link ?>"/>
            </div>
        <?php endif; ?>
        </div>

        <div class="it_b">
            <div class="it_content"><?php echo CHtml::encode($data->item_id); ?></div>
        </div>

        <div class="it_b">
            <div class="it_content"><?php echo CHtml::encode($data->price); ?></div>
        </div>

        <div class="it_b">
            <div class="it_content"><?php echo CHtml::encode($data->quantity); ?></div>
        </div>
        <div class="it_b">
            <div class="it_content"><?php echo CHtml::encode($data->weight); ?></div>
        </div>
        <div class="it_b">
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
                    ?>"/>
                </div>
            </div>
        </div>

        <div class="it_b">
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
                    ?>"/>
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
</a>
