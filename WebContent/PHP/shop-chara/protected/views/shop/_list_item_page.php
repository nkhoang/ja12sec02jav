<div id="upcomingMilestones">
    <h3 id="projectOverviewUpComingMilestones">
        <?php
                    $this->breadcrumbs = array(
            'Category' => array('/shop/index'),
            $category->title,
        );
        $this->widget('zii.widgets.CBreadcrumbs', array(
            'links' => $this->breadcrumbs,
            'homeLink' => false,
        ));
        ?><!-- breadcrumbs -->
    </h3>
</div>
<div id="projectActivity">


    <script type="text/javascript">
        var item_paging_url = '<?php echo CController::createUrl('/shop/renderItemList', array('category_id' => $category->id)); ?>'; // default value.;
        function handleDeleteItems() {
            var item = '';
            $('.label_checkbox input').each(function() {
                var input = $(this);
                if (input[0].checked) {
                    if (item.length != 0) {
                        item += ',';
                    }
                    item += input.attr('class');
                }
            });
            $.ajax({
                'url': '<?php echo CController::createUrl('/item/deleteItems');?>',
                'data': {
                    'delete_items': item
                },
                'type': 'post',
                'success': function(html) {
                    alert(html);
                    if (item_paging_url != null) {
                        $.ajax({
                            'url': item_paging_url,
                            'type': 'post',
                            'success': function(html) {
                                $('#items_c').html(html);
                                item_list_view_callback();
                            }
                        });
                    }
                }
            });
        }
        $(function() {
            // first time load item list view.
            $.ajax({
                'url': '<?php echo CController::createUrl('shop/renderItemList', array(
                    'category_id' => $category->id,
                ));?>',
                'data': {
                    'processOutput': true
                },
                'type': 'post',
                'success': function(html) {
                    $('#items_c').html(html);
                    item_list_view_callback();
                }
            });
        });
        function item_list_view_callback() {
            $('#item_board .sorter a, #item_board li.page a, #item_board li.next a, #item_board li.previous a').click(function() { // fix: save the state of list view paging.
                // update paging information
                item_paging_url = $(this).attr('href');
            });
            $("a.showItemForm").fancybox({
                'transitionIn'    :    'fade',
                'transitionOut'    :    'fade',
                'speedIn'        :    600,
                'speedOut'        :    200,
                'overlayShow'    :    true,
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
                                $('#items_c').html(html);
                                item_list_view_callback();
                            }
                        });
                    }
                }
            });
            buildTooltip('div.ic div.img_c .wraptocenter');
            $('label.label_checkbox').each(function() {
                var $this = $(this);
                var input = $this.find('input')[0];
                $this.addClass(input.checked ? 'label_checkbox r_on' : 'label_checkbox r_off');
                $this.bind('click', function(event) {
                    if ($this.hasClass('r_off') || !input.checked) {
                        $this.removeClass('r_off');
                        $this.addClass('r_on');
                        input.checked = true;
                    } else if ($this.hasClass('r_on') || input.checked) {
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
    <div class="content">
        <div id="item_board">

            <div id="item_controller">
                <a title="Add Item" class="showItemForm"
                   href="<?php echo CController::createUrl('/item/ajaxCreateItem', array('category_id' => $category->id)); ?>">
                    <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32"/></a>
                <a title="Delete Items" href="javascript:handleDeleteItems()"> <img
                        src="<?php echo Yii::app()->request->baseUrl . '/images/delete.png'; ?>" width="32"
                        height="32"/></a>
            </div>
            <div id="items_c">

            </div>
        </div>
        <div style="clear:both;"></div>
    </div>
</div>
