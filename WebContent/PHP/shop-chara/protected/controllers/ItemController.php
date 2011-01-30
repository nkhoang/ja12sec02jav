<?php

class ItemController extends Controller {

    /**
     * @var string the default layout for the views. Defaults to '//layouts/column2', meaning
     * using two-column layout. See 'protected/views/layouts/column2.php'.
     */
    public $layout = '//layouts/shop_column1';

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
            array('allow',
                'actions' => array('showGallery'),
                'users' => array('*'),
            ),
            array('allow',
                'actions' => array('ajaxCreateItem', 'ajaxEditItem', 'ajaxUpdate', 'getAllItems', 'deleteItems'),
                'users' => array('@'),
            ),
            array('allow',
                'actions' => array('delete'),
                'users' => array('nkhoang.it'),
            ),
            array('deny',
                'users' => array('*'),
            ),
        );
    }

    /**
     * May not use right now but it is a sample for JSON return type.
     */
    public function actionGetAllItems() {
        $categoryID = $_POST['category_id'];
        if (!isset($categoryID)) {
            echo CJSON::encode(array('errors' => 'Error.'));
        }
    }

    /**
     * Show item edit form.
     * @param  $item_id item id.
     * @return void .
     */
    public function actionAjaxEditItem($item_id = null) {
        $item = Item::model()->with('itemPictures')->findByPk($item_id);
        $item->category_prefix = substr($item->item_id, 0, 2);
        $item->number_part = substr($item->item_id, 2);

        $categories = Category::model()->findAll();

        $this->renderPartial('/item/_edit_form', array(
            'model' => $item,
            'itemID' => $item_id,
            'categories' => CHtml::listData($categories, 'id', 'title'),
            'prefix' => CHtml::listData($categories, 'category_code', 'category_code'),
            'performAction' => 'ajaxUpdate',
        ), false, true); // see documentation for this. very tricky.[IMPORTANT]
    }

    /**
     * Show item picture of an item.
     * @throws CHttpException may throw if invalid item id inputted.
     * @param  $item_id item id.
     * @return void
     */
    public function actionShowGallery($item_id = null) {
        if ($item_id === null) {
            throw new CHttpException(404, 'Invalid request.');
        }

        $item = Item::model()->findByPk($item_id);
        if (!isset($item)) {
            throw new CHttpException(404, 'Invalid request.');
        }
        $itemPictures = $item->itemPictures;
        $this->renderPartial('/item/_gallery_view', array(
            'itemPictures' => $itemPictures,
        ));
    }

    /**
     * create a new category.
     * @param  $category_id category id.
     * @return void
     */
    public function actionAjaxCreateItem($category_id = null) {
        $item = new Item;
        $item->category_id = (int) $category_id;
        $this->performAjaxValidation($item);

        if (Yii::app()->request->isAjaxRequest && isset($_POST['Item'])) {
            $item->attributes = $_POST['Item'];
            $item->item_id = $item->category_prefix . $item->number_part; // compose 2 parts of the item id.

            if ($item->save()) {
                Yii::app()->user->setFlash('itemSaved', 'Item Saved!!!!');
            }
        }

        $categories = array(
            Category::model()->findByPk($item->category_id),
        );

        if (isset($item->id)) {

        } else {
            $item->number_part = Category::getNextItemNumber($item->category_id);
        }
        $category = Category::model()->findByPk($item->category_id);
        $item->category_prefix = $category->category_code;

        $this->renderPartial('_simple_form', array(
            'model' => $item,
            'prefix' => CHtml::listData($categories, 'category_code', 'category_code'),
            'categories' => CHtml::listData($categories, 'id', 'title')),
            false, true);
    }

    /**
     * Delete item via AJAX.
     * @return void
     */
    public function actionDeleteItems() {
        $items = $_POST['delete_items'];
        if (!isset($items)) {
            echo 'Failed to serve your request.';
        }
        $delete_items = preg_split("/[\s,]+/", $items);

        foreach ($delete_items as $item_id) {
            $this->loadModel($item_id)->delete();
        }

        echo 'Your selected items have been deleted';
    }

    /**
     * Updates a particular model.
     * If update is successful, the browser will be redirected to the 'view' page.
     * @param integer $id the ID of the model to be updated
     */
    public function actionAjaxUpdate($item_id = null) {
        $model = $this->loadModel($item_id);

        // set other attributes
        $model->category_prefix = substr($model->item_id, 0, 2);
        $model->number_part = substr($model->item_id, 2);

        $this->performAjaxValidation($model);

        if (isset($_POST['Item'])) {
            $model->attributes = $_POST['Item'];

            $model->item_id = $model->category_prefix . $model->number_part; // compose 2 parts of the item id.
            if ($model->save()) {
                Yii::app()->user->setFlash('itemUpdated', 'Item Updated!!!!');
            }
        }

        // retrieve category list.
        $categories = Category::model()->findAll();

        $this->renderPartial('/item/_edit_form', array(
            'model' => $model,
            'itemID' => $id,
            'categories' => CHtml::listData($categories, 'id', 'title'),
            'prefix' => CHtml::listData($categories, 'category_code', 'category_code'),
            'performAction' => 'ajaxUpdate',
        ), false, true);
    }

    /**
     * Deletes a particular model.
     * If deletion is successful, the browser will be redirected to the 'index' page.
     * @param integer $id the ID of the model to be deleted
     */
    public function actionDelete($id) {
        if (Yii::app()->request->isPostRequest) {
            // we only allow deletion via POST request
            $this->loadModel($id)->delete();

            // if AJAX request (triggered by deletion via admin grid view), we should not redirect the browser
            if (!isset($_GET['ajax']))
                $this->redirect(isset($_POST['returnUrl']) ? $_POST['returnUrl'] : array('admin'));
        }
        else
            throw new CHttpException(400, 'Invalid request. Please do not repeat this request again.');
    }


    /**
     * Returns the data model based on the primary key given in the GET variable.
     * If the data model is not found, an HTTP exception will be raised.
     * @param integer the ID of the model to be loaded
     */
    public function loadModel($id) {
        $model = Item::model()->findByPk((int) $id);
        if ($model === null)
            throw new CHttpException(404, 'The requested page does not exist.');
        return $model;
    }

    /**
     * Performs the AJAX validation.
     * @param CModel the model to be validated
     */
    protected function performAjaxValidation($model) {
        if (isset($_POST['ajax']) && ($_POST['ajax'] === 'item-form' || $_POST['ajax'] === 'edit-item-form')) {
            echo CActiveForm::validate($model);
            Yii::app()->end();
        }
    }

}
