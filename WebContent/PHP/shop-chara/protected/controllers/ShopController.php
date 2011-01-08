<?php

class ShopController extends Controller {
    const CATEGORY_PAGE_SIZE = 20;
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

    public function actionShowItems($category_id = null) {
        // build search criteria
        $criteria = new CDbCriteria; // just to apply sort
        $criteria->select = '*';
        $criteria->condition = 'category_id=:categoryID';
        $criteria->params = array(
            ':categoryID' => $category_id,
        );

        $criteria->limit = self::ITEM_PAGE_SIZE;

        // build sort for data provider.
        $sort = new CSort('Item');
        $sort->sortVar = 'itemSort';
        $sort->defaultOrder = 'item_id ASC';
        $sort->params = array(
            'category_id' => $category_id,
        );
        $sort->attributes = array(
            'item_id' => array(
                'asc' => 'item_id ASC',
                'desc' => 'item_id DESC',
                'title' => 'Item ID',
                'default' => 'asc',
            ),
        );
        $sort->applyOrder($criteria); // apply criteria

        $count = count(Item::model()->findAll($criteria)); // count number result.

        $pages = new CPagination($count);
        $pages->pageSize = self::ITEM_PAGE_SIZE;
        $pages->pageVar = 'item_page';
        $pages->applyLimit($criteria); // get limit and offset
        $pages->setItemCount($count);
        $pages->params = array(
            'category_id' => $category_id,
        );

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
        $pageSize = $pager['pages']->getPageSize();
        $currentPage = $pager['pages']->getCurrentPage();
        // render list view widget for item list view.
        $itemsHTML = $this->widget('zii.widgets.CListView', array(
                    'id' => 'item_list_view',
                    'dataProvider' => $dataProvider,
                    'itemView' => '/item/_item_view',
                    'template' => '{items}',
                    'enableSorting' => false,
                    'enablePagination' => true,
                    'ajaxUpdate' => array('item_board'),
                    'pager' => $pager,
                    'sortableAttributes' => array(
                        'item_id' => 'Item ID',
                    ),
                        ), true);

        $this->renderPartial('/shop/_show_item', array(
            'itemsHTML' => $itemsHTML,
            'totalPage' => $pageSize,
            'currentPage' => $currentPage + 1,
        ));
    }

    public function actionViewItemDetails() {
        if (Yii::app()->request->isAjaxRequest && isset($_POST['item_id'])) {

            $item_id = (int) $_POST['item_id'];
            // get curren category from session
            $category_id = Yii::app()->session['item_category_index'];
            $category = Category::model()->findByPk($category_id);
            $item = Item::model()->findByPk($item_id);


            $this->renderPartial('/item/_item_details_view', array(
                'itemID' => $item_id,
                'categoryName' => $category->title,
                'itemID' => $item->item_id,
                    ), false, true);
        } else {
            throw new CHttpException(400, 'Invalid parameter.');
        }
    }

    /**
     * Show item Picture belong to an item id.
     * @param <type> $item_id  Item id of this item picture.
     */
    public function actionShowItemPicture($item_id = null) {
        if ($item_id === null) {
            if (isset(Yii::app()->session['item_item_picture_index'])) {
                $item_id = Yii::app()->session['item_item_picture_index'];
            }
        }
        // get category id from params
        if (Yii::app()->request->isAjaxRequest && isset($_POST['item_id'])) {
            $item_id = (int) $_POST['item_id'];
            Yii::app()->session['item_item_picture_index'] = $item_id;
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
                ), false, true);
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
                'actions' => array('index', 'listCategories', 'listItems', 'showItemPicture', 'viewItemDetails'),
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
     * The reason we have to resolve both POST and GET request is because we have POST request for admin board
     * and GET request for paging.
     * @param <type> $category_id category id.
     */
    public function actionListItems($categoryID = null) {
        if ($categoryID === null) {
            if (isset(Yii::app()->session['item_category_index'])) {
                $categoryID = Yii::app()->session['item_category_index'];
            }
        }
        // get category id from params
        if (Yii::app()->request->isAjaxRequest && isset($_POST['category_id'])) {
            $categoryID = (int) $_POST['category_id'];
            Yii::app()->session['item_category_index'] = $categoryID;
        }

        if (!isset($categoryID)) {
            echo 'Please select a category.'; //[IMPORTANT] do not end from here otherwise javascript file will not be registered.
        }
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

        $count = count(Item::model()->with(// load only item picture which have thumbnail flag.
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
        //
        // render list view widget for item list view.
        $itemListOutput = $this->widget('zii.widgets.CListView', array(
                    'id' => 'item_list_view',
                    'dataProvider' => $dataProvider,
                    'itemView' => '/item/_data_view',
                    'template' => '{sorter}{items} <div style="clear:both"></div>{pager}{summary}',
                    'summaryText' => 'Total: {count}', // @see CBaseListView::renderSummary(),
                    'enableSorting' => true,
                    'enablePagination' => true,
                    'ajaxUpdate' => array('item_board'),
                    'afterAjaxUpdate' => 'js:function(id, data){item_list_view_callback();}', // fix bug related to ajax content update with client javascript.
                    'pager' => $pager,
                    'sortableAttributes' => array(
                        'item_id' => 'Item ID',
                    ),
                        ), true);

        $category = Category::model()->findByPk($categoryID);

        $this->renderPartial('/shop/_list_item', array(
            'categoryID' => $categoryID,
            'categoryName' => $category->title,
            'itemListOutput' => $itemListOutput,
                ), false, true);
    }

    public function actionListCategories() {
        $processOutput = false;
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