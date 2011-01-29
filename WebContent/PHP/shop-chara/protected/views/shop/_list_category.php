<div id="projectActivity">
    <div class="content">
        <div id="category_board">
            <script type="text/javascript">
                $(function() {
                    category_list_view_callback();
                });
                function category_list_view_callback() {
                    $('#category_board .sorter a, #category_board li.page a,#category_board li.next a,#category_board li.previous a').click(function() { // fix: save the state of list view paging.
                        category_paging_url = $(this).attr('href'); // update paging information. Fix bug paging. Keep the paging state / sort state.
                    });

                    $("a.showCategoryCreateForm, a.showCategoryForm").fancybox({ // show fancybox.
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
                            $.ajax({
                                'url': category_paging_url, // post the cache url.
                                'type': 'post',
                                'success': function(html) {
                                    $('#admin_board').html(html);
                                }
                            });
                        }
                    });
                }
            </script>

            All chara categories are listed below, you can click on category to list its items:
            <div id="category_controller">
                <a title="Creat a new category" class="showCategoryCreateForm"
                   href="<?php echo CController::createUrl('/category/ajaxUpdateCategory'); ?>"> <img
                        src="<?php echo Yii::app()->request->baseUrl . '/images/add.png'; ?>" width="32"
                        height="32"/></a>
            </div>
        <?php echo $category_list; ?>
        </div>
        <div style="clear:both;"></div>
    </div>
</div>

