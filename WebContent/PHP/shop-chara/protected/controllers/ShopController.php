<?php

class ShopController extends Controller {
    const CATEGORY_PAGE_SIZE = 2;
    const ITEM_PAGE_SIZE = 2;
    const ITEM_PICTURE_PAGE_SIZE = 2;

    public $layout = "//layouts/shop_column1";

    /**
     * @return array action filters
     */
    public function filters() {
        return array(
            'accessControl', // perform access control for CRUD operations
        );
    }

    /**
     * Show items in main index page.
     * @param <type> $category_id category id.
     * @param <type> $item_page current page.
     */
    public function actionShowItems($category_id = null, $item_page = 1) {
        // build search criteria
        $criteria = new CDbCriteria; // just to apply sort
        $criteria->select = '*';
        $criteria->condition = 'category_id=:categoryID';
        $criteria->params = array(
            ':categoryID' => $category_id,
        );
        $criteria->with = 'itemPictures';

        // build sort for data provider.
        $sort = new CSort('Item');
        $sort->sortVar = 'itemSort';
        $sort->multiSort = true;
        $sort->defaultOrder = array('item_id asc', 'is_thumbnail_picture desc');
        $sort->attributes = array(
            'item_id' => array(
                'asc' => 'item_id asc',
                'desc' => 'item_id desc',
                'title' => 'Item ID',
                'default' => 'asc',
            ),
            'is_thumbnail_picture' => array(
                'asc' => 'itemPictures.is_thumbnail_picture asc',
                'desc' => 'itemPictures.is_thumbnail_picture desc',
                'title' => 'Item Pictures',
                'default' => 'desc',
            ),
        );
        $sort->applyOrder($criteria); // apply criteria

        $count = count(Item::model()->findAll($criteria)); // count number result.

        $pages = new CPagination($count);
        $pages->pageSize = self::ITEM_PAGE_SIZE;
        $pages->pageVar = 'item_page';
        $pages->applyLimit($criteria); // get limit and offset
        $pages->setItemCount($count);

        $dataProvider = new CActiveDataProvider('Item',
            array(
                'criteria' => $criteria,
                'pagination' => $pages,
                'sort' => $sort,
            )
        );
        $pager = array(
            'htmlOptions' => array(
                'id' => 'item_pager',
            ),
        );
        $pager['pages'] = $dataProvider->getPagination(); //$pager['pages']->getPageCount()

        $pageCount = $pager['pages']->getPageCount();
        $currentPage = $item_page;
        // render list view widget for item list view.
        $itemsHTML = $this->widget('zii.widgets.CListView', array(
            'id' => 'item_list_view',
            'dataProvider' => $dataProvider,
            'itemView' => '/item/_item_view',
            'template' => '{items}',
            'enableSorting' => false,
            'enablePagination' => true,
            'ajaxUpdate' => array('itemsContainer'),
            'pager' => $pager,
            'sortableAttributes' => array(
                'item_id' => 'Item ID',
            ),
        ), true);

        $this->renderPartial('/shop/_show_item', array(
            'itemsHTML' => $itemsHTML,
            'totalPage' => $pageCount,
            'currentPage' => $currentPage,
        ));
    }


    /**
     * Show details view of an item. Must provide item id.
     * @param <type> $item_id item id.
     */
    public function actionViewItemDetails($item_id = null) {
        if ($item_id === null) { // check parameter.
            throw new CHttpException(400, 'Invalid parameter.');
        }
        $item = Item::model()->findByPk($item_id);
        if (!isset($item)) { // wrong id.
            throw new CHttpException(400, 'Invalid parameter.');
        }
        $category = Category::model()->findbyPk($item->category_id);

        $itemThumbnail = ItemPicture::model()->find(array( // find just one
            'condition' => 'item_id=:itemID AND is_thumbnail_picture=:isThumbnail',
            'params' => array(
                ':itemID' => $item->id,
                ':isThumbnail' => 1,
            ),
        ));
        $this->render('/item/_item_details_view', array(
            'category' => $category,
            'item' => $item,
            'itemThumbnailLink' => $itemThumbnail->link,
        ));
    }

    /**
     * Reload item picture for Item details view page.
     * @param <type> $item_id item id.
     */
    public function actionReloadThumbnailPicture($item_id = null) {
        if ($item_id === null) {
            throw new Exception(404, 'Your request is invalid');
        }
        $item = Item::model()->findByPk($item_id);
        if (!isset($item)) {
            throw new Exception(404, 'Your request is invalid');
        }
        echo $item->getThumbnailPicture()->link;
    }

    /**
     * Show item Picture belong to an item id.
     * @param <type> $item_id  Item id of this item picture.
     */
    public function actionShowItemPicture($item_id = null) {
        $processOutput = false;
        if ($_POST['processOutput']) {
            $processOutput = $_POST['processOutput'];
        }
        // build search criteria
        $criteria = new CDbCriteria; // just to apply sort
        $criteria->select = '*';
        $criteria->condition = 'item_id=:itemID';
        $criteria->params = array(
            ':itemID' => $item_id,
        );

        $criteria->limit = self::ITEM_PICTURE_PAGE_SIZE;

        // build sort for data provider.
        $sort = new CSort('ItemPicture');
        $sort->sortVar = 'itemPictureSort';
        $sort->defaultOrder = 'title ASC';
        $sort->attributes = array(
            'title' => array(
                'asc' => 'title ASC',
                'desc' => 'title DESC',
                'title' => 'Title',
                'default' => 'asc',
            ),
        );
        $sort->applyOrder($criteria); // apply criteria

        $count = count(ItemPicture::model()->findAll($criteria)); // count number result.

        $pages = new CPagination($count);
        $pages->pageSize = self::ITEM_PICTURE_PAGE_SIZE;
        $pages->pageVar = 'item_picture_page';
        $pages->applyLimit($criteria); // get limit and offset
        $pages->setItemCount($count);

        $dataProvider = new CActiveDataProvider('ItemPicture',
            array(
                'criteria' => $criteria,
                'pagination' => $pages,
                'sort' => $sort,
            )
        );
        $pager = array(
            'htmlOptions' => array(
                'id' => 'item_picture_pager',
            ),
        );
        $pager['pages'] = $dataProvider->getPagination(); //$pager['pages']->getPageCount()

        $this->renderPartial('/shop/_list_item_picture', array(
            'dataProvider' => $dataProvider,
            'pager' => $pager,
        ), false, $processOutput);
    }

    /**
     * Specifies the access control rules.
     * This method is used by the 'accessControl' filter.
     * @return array access control rules
     */
    public function accessRules() {
        return array(
            array('allow',
                'actions' => array('showItems'),
                'users' => array('*'),
            ),
            array('allow', // allow authenticated user to perform 'create' and 'update' actions
                'actions' => array('index', 'reloadThumbnailPicture', 'listCategories', 'listItems', 'showItemPicture', 'viewItemDetails', 'renderItemList'),
                'users' => array('@'),
            ),
            array('deny', // deny all users
                'users' => array('*'),
            ),
        );
    }

    public function actionIndex() {
        $item = new Item;
        Yii::app()->clientScript->registerScript('register_static_css_js', "
            $(function() {
                 script_files = $('script[src]').map(function() { return $(this).attr('src'); }).get();
                 css_files = $('link[href]').map(function() { return $(this).attr('href'); }).get();
            });");
        $this->render('index', array(
            'item' => $item,
        ));
    }

    /**
     * Render list item page.
     * @throws CHttpException
     * @param  $category_id
     * @return void
     */
    public function actionRenderItemList($category_id = null) {
        $processOutput = false;
        if ($_POST['processOutput']) { // detect if JS should be rendered with page.
            $processOutput = $_POST['processOutput'];
        }
        if ($category_id === null) { // no category specified => throw exception.
            throw new CHttpException(400, 'Invalid request.');
        }
        $categoryID = $category_id;
        // build search criteria
        $criteria = new CDbCriteria; // just to apply sort
        $criteria->select = '*';
        $criteria->condition = 'category_id=:categoryID';
        $criteria->params = array(
            ':categoryID' => $categoryID,
        );

        $criteria->limit = self::ITEM_PAGE_SIZE;

        // build sort for data provider.
        $sort = new CSort('Item');
        $sort->sortVar = 'itemSort';
        $sort->defaultOrder = 'item_id ASC';
        $sort->attributes = array(
            'item_id' => array(
                'asc' => 'item_id ASC',
                'desc' => 'item_id DESC',
                'title' => 'Item ID',
                'default' => 'asc',
            ),
        );
        $sort->applyOrder($criteria); // apply criteria

        $count = count(Item::model()->with( // load only item picture which have thumbnail flag.
            array(
                'itemPictures' => array(
                    'condition' => 'is_thumbnail_picture=:thumbnailFlag',
                    'params' => array(
                        ':thumbnailFlag' => 1,
                    ),
                ),
            ))->findAll($criteria)); // count number result.

        $pages = new CPagination($count);
        $pages->pageSize = self::ITEM_PAGE_SIZE;
        $pages->pageVar = 'item_page';
        $pages->applyLimit($criteria); // get limit and offset
        $pages->setItemCount($count);

        $dataProvider = new CActiveDataProvider('Item',
            array(
                'criteria' => $criteria,
                'pagination' => $pages,
                'sort' => $sort,
            )
        );
        $pager = array(
            'htmlOptions' => array(
                'id' => 'item_pager',
            ),
        );
        $pager['pages'] = $dataProvider->getPagination(); //$pager['pages']->getPageCount()

        // construct the header of the table.
        $table_header = '<div class="ihc">
               <div class="it_h">
                   ' . 'Delete ?' . '
               </div>
               <div class="it_h img_c">
                    ' . 'Thumbnail' . '
               </div>
               <div class="it_h">
                   ' . CHtml::encode(Item::model()->getAttributeLabel("item_id")) . '
               </div><div class="it_h">
                    ' . CHtml::encode(Item::model()->getAttributeLabel("price")) . '
               </div><div class="it_h">
                   ' . CHtml::encode(Item::model()->getAttributeLabel("quantity")) . '
               </div><div class="it_h">
                   ' . CHtml::encode(Item::model()->getAttributeLabel("weight")) . '
               </div><div class="it_h">
                   ' . CHtml::encode(Item::model()->getAttributeLabel("is_hot")) . '
                </div><div class="it_h">
                   ' . CHtml::encode(Item::model()->getAttributeLabel("is_discounting")) . '
                </div><div class="it_h">
                   ' . CHtml::encode(Item::model()->getAttributeLabel("size")) . '
                </div><div class="it_h">
                   ' . CHtml::encode(Item::model()->getAttributeLabel("material")) . '

           </div></div>';

        // render list view widget for item list view.
        $item_list_view = $this->widget('zii.widgets.CListView', array(
            'id' => 'item_list_view',
            'dataProvider' => $dataProvider,
            'itemView' => '/item/_data_view',
            'template' => '{sorter}' . $table_header . '{items} <div style="clear:both"></div>{pager}<br />{summary}',
            'summaryText' => 'Total Items: {count}', // @see CBaseListView::renderSummary(),
            'enableSorting' => true,
            'enablePagination' => true,
            'ajaxUpdate' => array('item_board'),
            'afterAjaxUpdate' => 'js:function(id, data){item_list_view_callback();}', // fix bug related to ajax content update with client javascript.
            'pager' => $pager,
            'sortableAttributes' => array(
                'item_id' => 'Item ID',
            ),
        ), true);

        $this->renderPartial('/shop/_item_list_view', array(
            'item_list_view' => $item_list_view,
        ), false, $processOutput);
    }

    /**
     * Render list item page. Item list view will be render via AJAX.
     * @param <type> $category_id category id.
     */
    public function actionListItems($category_id = null) {
        $category = Category::model()->findByPk($category_id);

        $this->render('/shop/_list_item_page', array(
            'category' => $category,
        ));
    }

    /**
     * List current available categories.
     * AJAX called from 'shop/index'.
     */
    public function actionListCategories() {
        $processOutput = false; // handle for fancybox onClosed event.
        if ($_POST['processOutput']) {
            $processOutput = true;
        }
        // build search criteria
        $criteria = new CDbCriteria; // just to apply sort
        $criteria->select = '*';
        $criteria->limit = self::CATEGORY_PAGE_SIZE;

        // build sort for data provider.
        $sort = new CSort('Category');
        $sort->sortVar = 'categorySort';
        $sort->defaultOrder = 'title ASC';
        $sort->attributes = array(
            'title' => array(
                'asc' => 'title ASC',
                'desc' => 'title DESC',
                'title' => 'Title',
                'default' => 'asc',
            ),
        );
        $sort->applyOrder($criteria); // apply criteria

        $count = count(Category::model()->findAll($criteria)); // count number result.

        $pages = new CPagination($count);
        $pages->pageSize = self::CATEGORY_PAGE_SIZE;
        $pages->pageVar = 'category_page';
        $pages->applyLimit($criteria); // get limit and offset
        $pages->setItemCount($count);

        $dataProvider = new CActiveDataProvider('Category',
            array(
                'criteria' => $criteria,
                'pagination' => $pages,
                'sort' => $sort,
            )
        );
        $pager = array(
            'htmlOptions' => array(
                'id' => 'category_pager',
            ),
        );
        $pager['pages'] = $dataProvider->getPagination(); //$pager['pages']->getPageCount()

        $categoryListHTML = $this->widget('zii.widgets.CListView', array(
            'id' => 'category_list_view',
            'dataProvider' => $dataProvider,
            'itemView' => '/category/_data_view', // refers to the partial view named '_post'
            'template' => '{sorter}{items} <div style="clear:both"></div>{pager}{summary}',
            'summaryText' => 'Total: {count}', // @see CBaseListView::renderSummary(),
            'enableSorting' => true,
            'ajaxUpdate' => array('category_board'),
            'afterAjaxUpdate' => 'js:function(id, data){category_list_view_callback();}', // fix bug related to ajax content update with client javascript.
            'enablePagination' => true,
            'pager' => $pager,
            'sortableAttributes' => array(
                'title' => 'Title',
            ),
        ), true);
        $this->renderPartial('/shop/_list_category', array(
            'category_list' => $categoryListHTML,
        ), false, $processOutput);
    }


    /**
     * Performs the AJAX validation.
     * @param CModel the model to be validated
     */
    protected function performAjaxValidation($model) {
        if (isset($_POST['ajax']) && ($_POST['ajax'] === 'item-form')) {
            echo CActiveForm::validate($model);
            Yii::app()->end();
        }
    }

}