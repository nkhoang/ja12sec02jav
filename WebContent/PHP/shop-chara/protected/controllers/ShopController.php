<?php

class ShopController extends Controller {

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
                'actions' => array('index', 'ajaxCreateItem', 'ajaxCreateItemPicture'),
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

    public function actionAjaxCreateItem() {
        $item = new Item;
        $this->performAjaxValidation($item);

        if (Yii::app()->request->isAjaxRequest && isset($_POST['Item'])) {
            $item->attributes = $_POST['Item'];
            if ($item->save()) {
                $this->redirect(array('/shop/ajaxCreateItemPicture'));
            }
        }
        $this->renderPartial('/item/_simple_form', array('model' => $item), false, true);
    }

    public function actionAjaxCreateItemPicture() {
        $itemPic = new ItemPicture;
        $this->performAjaxValidation($itemPic);

        if (Yii::app()->request->isAjaxRequest && isset($_POST['ItemPicture'])) {
            $itemPic->attributes = $_POST['ItemPicture'];
            if ($itemPic->save()) {

            }
        }

        $this->renderPartial('/itemPicture/_simple_form', array('model' => $itemPic), false, true);
    }

    /**
     * Performs the AJAX validation.
     * @param CModel the model to be validated
     */
    protected function performAjaxValidation($model) {
        if (isset($_POST['ajax']) && ($_POST['ajax'] === 'item-form') || ($_POST['ajax'] === 'item-picture-form')) {
            echo CActiveForm::validate($model);
            Yii::app()->end();
        }
    }

}