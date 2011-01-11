<div class="content">
    <?php
    $this->widget('zii.widgets.CListView', array(
        'id' => 'item_picture_list_view',
        'dataProvider' => $dataProvider,
        'itemView' => '/itemPicture/_data_view', // refers to the partial view named '_post'
        'template' => '{sorter}{items} <div style="clear:both"></div>{pager}{summary}',
        'summaryText' => 'Total: {count}', // @see CBaseListView::renderSummary(),
        'enableSorting' => true,
        'enablePagination' => true,
        'ajaxUpdate' => array('item_pictures'),
        'afterAjaxUpdate' => 'js:function(id, data){item_picture_list_view_callback();}', // fix bug related to ajax content update with client javascript.
        'pager' => $pager,
        'sortableAttributes' => array(
            'title' => 'Title',
        ),
    ));
    ?>
    <div style="clear:both;"></div>
</div>
