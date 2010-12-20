<?php

class ShopController extends Controller {
    const CATEGORY_PAGE_SIZE = 1;

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
                'actions' => array('index', 'ajaxCreateItem', 'ajaxCreateItemPicture', 'listCategories'),
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

    public function actionListCategories() {
        // build search criteria
        $criteria = new CDbCriteria; // just to apply sort
        $criteria->select = '*';
        $criteria->limit = self::CATEGORY_PAGE_SIZE;

        // build sort for data provider.
        $sort = new CSort('Category');
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
        $pages->applyLimit($criteria); // get limit and offset
        $pages->setItemCount($count);

        $dataProvider = new CActiveDataProvider('Category',
                        array(
                            'criteria' => $criteria,
                            'pagination' => $pages,
                            'sort' => $sort,
                        )
        );
        $pager = array();
        $pager['pages'] = $dataProvider->getPagination(); //$pager['pages']->getPageCount()

        $this->widget('zii.widgets.CListView', array(
            'dataProvider' => $dataProvider,
            'itemView' => '/category/data_view', // refers to the partial view named '_post'
            'template' => '{sorter}{items} <div style="clear:both"></div>{pager}{summary}',
            'summaryText' => 'Total: {count}', // @see CBaseListView::renderSummary(),
            'enableSorting' => true,
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