<!-- Item Controller -->
<div id="item_controller">
    <a title="Add Item" id="showItemForm" href="<?php echo CController::createUrl('/item/ajaxCreateItem', array('category_id' => $categoryID)); ?>"> <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32" /></a>
</div>


<script type="text/javascript" >

    $(function(){
        $("#showItemForm").fancybox({
            'transitionIn'	:	'fade',
            'transitionOut'	:	'fade',
            'speedIn'		:	600,
            'speedOut'		:	200,
            'overlayShow'	:	true,
            'centerOnScroll': true,
            'type' : 'ajax',
            'ajax' : {
                type: "POST"
            },
            'onClosed': function() {
                <?php echo $scripts ?>
            }
        });
        buildTooltip('div.ic div.img_c .wraptocenter');


        $('label.label_radio').each(function(){
            var $this = $(this);
            var input = $this.find('input')[0];
            $this.addClass((input.checked == true || input.checked) ? 'label_radio r_on' : 'label_radio r_off');
            $this.bind('click', function(event){
                if ($this.hasClass('r_off') || input.checked) {
                    $this.removeClass('r_off');
                    $this.addClass('r_on');
                    input.checked = true;
                } else if ($this.hasClass('r_on') || !input.checked) {
                    $this.removeClass('r_on');
                    $this.addClass('r_off');
                    input.checked = false;
                }
                event.stopPropagation();
                event.preventDefault();
            });
        });
    });

</script>
<div id="items_c">
    <?php
    echo $itemListOutput;
    ?>

</div>