<div class="content">
    <div id="item_board">
        <?php
        $this->breadcrumbs = array(
            'Category' => array('/shop/listCategories'),
            $categoryName,
        );
        ?>

        <?php
        $this->widget('zii.widgets.CBreadcrumbs', array(
            'links' => $this->breadcrumbs,
            'homeLink' => false,
        ));
        ?><!-- breadcrumbs -->
        <!-- Item Controller -->
        <div id="item_controller">
            <a title="Add Item" class="showItemForm" href="<?php echo CController::createUrl('/item/ajaxCreateItem', array('category_id' => $categoryID)); ?>"> <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32" /></a>
        </div>
        <script type="text/javascript" >

            $(function(){
                item_list_view_callback();

                $('div.breadcrumbs a').click(function(e){ // using ajax to load content.
                    $.ajax({
                        'url': $(this).attr('href'),
                        'type': 'post',
                        'success': function(html){
                            $('#admin_board').html(html);
                        }
                    });
                    e.preventDefault();
                });
            });
            function item_list_view_callback(){
                $('#item_board .sorter a, #item_board li.page a, #item_board li.next a, #item_board li.previous a').click(function(){ // fix: save the state of list view paging.
                    // update paging information
                    item_paging_url = $(this).attr('href');
                });
                $("a.showItemForm").fancybox({
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
                        if (item_paging_url != null) {
                            $.ajax({
                                'url': item_paging_url,
                                'type': 'post',
                                'success': function(html) {
                                    $('#admin_board').html(html);
                                }
                            });
                        }
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
            }

        </script>
        <div id="items_c">
            <?php
            echo $itemListOutput;
            ?>

        </div>
    </div>
    <div style="clear:both;"></div>
</div>