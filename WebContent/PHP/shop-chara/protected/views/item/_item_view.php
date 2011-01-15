<script type="text/javascript">
    $(function(){
        /*
        $('a.itemSubPic').fancybox({ // enable fancy box
            titleShow: true,
            titlePosition: 'over',
            onComplete: function() {
                $("#fancybox-wrap").hover(function() {
                    $("#fancybox-title").show();
                }, function() {
                    $("#fancybox-title").hide();
                });
            }
        });
         */
        $('a.item_link').fancybox({
            'speedIn'		:	600,
            'speedOut'		:	200,
            'overlayShow'	:	true,
            'centerOnScroll': true,
            'type' : 'ajax',
            'onComplete': function(html) {
                loadGalleria();
            }
        });
    });
</script>
<div class="itemContainer fleft"><div class="itt fleft">
        <div class="itl fleft">
        </div>
        <div class="itc fleft">
        </div>
        <div class="itr fleft">
        </div>
    </div>
    <div class="itm fleft">
        <div class="itl fleft">
        </div>
        <div class="itc fleft">
            <!-- Item cover -->
            <div class="itemCover">
                <div class="ict fleft">
                    <div class="icl fleft">
                    </div>
                    <div class="icc fleft">
                    </div>
                    <div class="icr fleft">
                    </div>
                </div>
                <div class="icm fleft">
                    <div class="icl fleft">
                    </div>
                    <div class="icc fleft">
                        <div class="thumbnail ">
                            <div class="wraptocenter loading">

                                <a class="item_link" title="Image Gallery"
                                   href="<?php echo CController::createUrl('/item/showGallery', array('item_id' => $data->id)); ?>">
                                       <?php foreach ($data->itemPictures as $itemPicture): ?>
                                       <?php if ($itemPicture->is_thumbnail_picture === '1'): ?>
                                    <img src="<?php echo $itemPicture->link; ?>" alt="<?php echo CHtml::encode($data->description);?>" title="<?php echo CHtml::encode($data->description);?>" class="item"/>
                                    <?php break; ?>
                                    <?php endif; ?>
                                    <?php endforeach; ?>

                                           </a>

                                       </div>
                                   </div>
                                   <div class="thumbnail-big"><img src="<?php echo $data->itemPictures[0]->link; ?>"></div>
                        <?php foreach ($data->itemPictures as $itemPicture): ?>
                                                   <div class="subPictures"><a title="<?php echo $itemPicture->title; ?>" href="<?php echo $itemPicture->link; ?>" rel="subPicGroup-<?php echo $data->id; ?>" class="itemSubPic subPicGroup-<?php echo $data->id; ?>"></a></div>
                        <?php endforeach; ?>

                                                   <!-- Item code -->
                                                   <div class="itemCode fleft">
                                                       <img src="<?php echo Yii::app()->request->baseUrl . '/images/chara/item_code.gif' ?>" height="15" width="125">
                                                       <div class="codeDescription fleft">
                                                           <div class="codeName"><?php echo $data->item_id ?></div>
                                                       </div>
                                                   </div>
                                               </div>
                                               <div class="icr fleft">
                                               </div>
                                           </div>
                                           <div class="icb fleft">
                                               <div class="icl fleft">
                                               </div>
                                               <div class="icc fleft">
                                               </div>
                                               <div class="icr fleft">
                                               </div>
                                           </div>
                                       </div>
                                   </div>
                                   <div class="itr fleft">
                                   </div>
                               </div>
                               <div class="itb fleft">
                                   <div class="itl fleft">
                                   </div>
                                   <div class="itc fleft">
                                   </div>
                                   <div class="itr fleft">
                                   </div>
                               </div>
                               <div class="sticker fleft">
                                   <img src="<?php echo Yii::app()->request->baseUrl . '/images/chara/sticker.png' ?>" height="57" width="56">
                               </div>
                               <div class="itemTag fleft">
                                   <img src="<?php echo Yii::app()->request->baseUrl . '/images/chara/item_tag.png' ?>" height="65" width="47">
                                   <div class="itemPrice">
                                       <div class="price"><?php echo $data->price; ?></div>
        </div>
    </div>
</div>