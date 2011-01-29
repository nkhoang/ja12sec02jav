<?php

class CategoryController extends Controller {
    public $layout = '//layouts/column2';

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
                'actions' => array('ajaxUpdateCategory', 'deleteCategory'),
                'users' => array('@'),
            ),
            array('deny', // deny all users
                'users' => array('*'),
            ),
        );
    }

    /**
     * Delete category id. Using GET parameter.
     * @param <type> $category_id category id.
     */
    public function actionDeleteCategory($category_id = null) {
        if ($category_id === null) { // must specified the category id by GET parameter.
            throw new CHttpException(404, 'The requested page does not exist.'); // throws Exception instead.
        }
        $items = Item::model()->findAll('category_id =:categoryID', array(
            ':categoryID' => $category_id,
        ));
        if (!isset($items) || sizeof($items) == 0) { // make sure that returned items are not empty.
            throw new CHttpException(404, 'Could not find category id. Request rejected.'); // throws Exception instead.
        } else if (count($items) > 0) {
            foreach ($items as $item) {
                $item->delete(); // loop to delete.
            }
        }
        $this->loadModel($category_id)->delete();
        echo 'success';
    }

    /**
     * update/add new category id.
     * @param <type> $id category_id
     */
    public function actionAjaxUpdateCategory($id = null) {
        if ($id !== null) {
            $model = $this->loadModel($id); // in case update.
        } else {
            $model = new Category; // in case add new.
        }

        // Uncomment the following line if AJAX validation is needed
        $this->performAjaxValidation($model);

        if (isset($_POST['Category'])) {
            $model->attributes = $_POST['Category'];
            if (isset($model->id)) { // check to make sure that category is in EDIT mode.
                $old_category_code = $this->loadModel($model->id)->category_code; // should change category code accordingly.
                if (isset($old_category_code) && strlen($old_category_code) > 0) { // the old category code may be null or blank.
                    if ($model->category_code !== $old_category_code) { // need to change others accordingly.
                        $items = Item::model()->findAll('category_id = :categoryID', array( // search items which need to be changed.
                            ':categoryID' => $model->id,
                        ));
                        foreach ($items as $item) { // actually it is only one, loop to make sure the code logic.
                            $updated_item = Item::changeCategoryCodeFromId($item, $model->category_code);
                            if (isset($updated_item)) {
                                if ($updated_item->update('item_id')) { // save updated item.
                                }
                            }
                        }
                    }
                }
            }
            if ($model->save()) {
                Yii::app()->user->setFlash('categoryUpdated', 'Category Updated!!!!'); // update flash.
            }
        }
        $this->renderPartial('/category/_simple_form', array(
            'model' => $model,
        ), false, true);
    }

    /**
     * Returns the data model based on the primary key given in the GET variable.
     * If the data model is not found, an HTTP exception will be raised.
     * @param integer the ID of the model to be loaded
     */
    public function loadModel($id) {
        $model = Category::model()->findByPk((int) $id);
        if ($model === null)
            throw new CHttpException(404, 'The requested page does not exist.');
        return $model;
    }

    /**
     * Performs the AJAX validation.
     * @param CModel the model to be validated
     */
    protected function performAjaxValidation($model) {
        if (isset($_POST['ajax']) && $_POST['ajax'] === 'category-form') {
            echo CActiveForm::validate($model);
            Yii::app()->end();
        }
    }

}