<?php

class ShopController extends Controller {
    const CATEGORY_PAGE_SIZE = 20;
    const ITEM_PAGE_SIZE = 20;
    const ITEM_PICTURE_PAGE_SIZE = 20;

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
     * Render view item details page to item board.
     * @params POST param named item_id to receive item id.
     */
    public function actionViewItemDetails() {
        if (Yii::app()->request->isAjaxRequest && isset($_POST['item_id'])) {
            $itemID = (int) $_POST['item_id'];
            $item = Item::model()->with('itemPictures')->findByPk($itemID);

            $item->category_prefix = substr($item->item_id, 0, 2);
            $item->number_part = substr($item->item_id, 2);

            $itemThumbnail = ItemPicture::model()->find(array(// find just one
                        'condition' => 'item_id=:itemID AND is_thumbnail_picture=:isThumbnail',
                        'params' => array(
                            ':itemID' => $itemID,
                            ':isThumbnail' => 1,
                        ),
                    ));

            // build search criteria
            $criteria = new CDbCriteria; // just to apply sort
            $criteria->select = '*';
            $criteria->condition = 'item_id=:itemID';
            $criteria->params = array(
                ':itemID' => $itemID,
            );

            $criteria->limit = self::ITEM_PICTURE_PAGE_SIZE;

            // build sort for data provider.
            $sort = new CSort('ItemPicture');
            $sort->sortVar = 'itemPictureSort';
            $sort->defaultOrder = 'item_id ASC';
            $sort->params = array(
                'item_id' => $itemID,
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

            $count = count(ItemPicture::model()->findAll($criteria)); // count number result.

            $pages = new CPagination($count);
            $pages->pageSize = self::ITEM_PICTURE_PAGE_SIZE;
            $pages->pageVar = 'item_picture_page';
            $pages->applyLimit($criteria); // get limit and offset
            $pages->setItemCount($count);
            $pages->params = array(
                'item_id' => $itemID,
            );

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

            $categories = Category::model()->findAll();

            $this->renderPartial('/item/_widget_data_view', array(
                'model' => $item,
                'itemID' => $itemID,
                'categories' => CHtml::listData($categories, 'id', 'title'),
                'prefix' => CHtml::listData($categories, 'category_code', 'category_code'),
                'itemThumbnailLink' => $itemThumbnail === null ? 'abc.link' : $itemThumbnail->link,
                'itemPicturesDataProvider' => $dataProvider,
                'itemPicturePager' => $pager,
            ), false, true);
        } else {
            throw new CHttpException(403, Yii::t('yii', 'Missing argument: itemID to render widget.'));
        }
    }

    /**
     * Specifies the access control rules.
     * This method is used by the 'accessControl' filter.
     * @return array access control rules
     */
    public function accessRules() {
        return array(
            array('allow', // allow authenticated user to perform 'create' and 'update' actions
                'actions' => array('index', 'listCategories', 'listItems', 'viewItemDetails'),
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

    /**
     * The reason we have to resolve both POST and GET request is because we have POST request for admin board
     * and GET request for paging.
     * @param <type> $category_id category id.
     */
    public function actionListItems($category_id = null) {
        if ($category_id !== null) {
            $categoryID = $category_id;
        }
        // get category id from params
        if (Yii::app()->request->isAjaxRequest && isset($_POST['category_id'])) {
            $categoryID = (int) $_POST['category_id'];
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
        $pages->params = array(
            'category_id' => $categoryID,
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
                    'pager' => $pager,
                    'sortableAttributes' => array(
                        'item_id' => 'Item ID',
                    ),
                        ), true);

        $this->renderPartial('/shop/_list_item', array(
            'categoryID' => $categoryID,
            'itemListOutput' => $itemListOutput,
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