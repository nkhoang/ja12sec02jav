<?php if ($dataProvider->getTotalItemCount(true) > 0) : ?>
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
                }
            });

        });

    </script>
    <div id="item_controller">
        <a title="Add Item" id="showItemForm" href="<?php echo CController::createUrl('/item/ajaxCreateItem'); ?>"> <img src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32" height="32" /></a>
    </div>
<?php endif; ?>
    <div id="item_list">
    <?php
    $this->widget('zii.widgets.CListView', array(
        'id' => 'item_list_view',
        'dataProvider' => $dataProvider,
        'itemView' => '/item/_data_view', // refers to the partial view named '_post'
        'template' => '{sorter}{items} <div style="clear:both"></div>{pager}{summary}',
        'summaryText' => 'Total: {count}', // @see CBaseListView::renderSummary(),
        'enableSorting' => true,
        'enablePagination' => true,
        'ajaxUpdate' => array('item_board'),
        'pager' => $pager,
        'sortableAttributes' => array(
            'item_id' => 'Item ID',
        ),
    ));
    ?>

</div>