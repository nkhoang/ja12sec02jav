<?php

class ShopController extends Controller {
    const CATEGORY_PAGE_SIZE = 1;
    const ITEM_PAGE_SIZE = 2;

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
     * Specifies the access control rules.
     * This method is used by the 'accessControl' filter.
     * @return array access control rules
     */
    public function accessRules() {
        return array(
            array('allow', // allow authenticated user to perform 'create' and 'update' actions
                'actions' => array('index', 'ajaxCreateItem', 'ajaxCreateItemPicture', 'listCategories', 'listItems'),
                'users' => array('@'),
            ),
            array('deny', // deny all users
                'users' => array('*'),
            ),
        );
    }

    public function actionIndex() {
        $item = new Item;
        $this->render('index', array(
            'item' => $item,
        ));
    }

    public function actionListItems($category_id = null) {
        $categoryID = -1; // never have category with ID less than 0.
        if ($category_id !== null)
            $categoryID = $category_id;

        // get category id from params
        if (Yii::app()->request->isAjaxRequest && isset($_POST['category_id'])) {
            $categoryID = (int) $_POST['category_id'];
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
        $sort->params = array(
            'category_id' => $categoryID,
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
        //$pager->htmlOptions = array(
        //    'id' => 'item_pager',
        //    'class' => 'abcdef',
        //);

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
    }

    public function actionListCategories() {
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

        $this->widget('zii.widgets.CListView', array(
            'id' => 'category_list_view',
            'dataProvider' => $dataProvider,
            'itemView' => '/category/_data_view', // refers to the partial view named '_post'
            'template' => '{sorter}{items} <div style="clear:both"></div>{pager}{summary}',
            'summaryText' => 'Total: {count}', // @see CBaseListView::renderSummary(),
            'enableSorting' => true,
            'ajaxUpdate' => array('category_board'),
            'enablePagination' => true,
            'pager' => $pager,
            'sortableAttributes' => array(
                'title' => 'Title',
            ),
        ));
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