<?php

class CategoryController extends Controller {

    /**
     * @var string the default layout for the views. Defaults to '//layouts/column2', meaning
     * using two-column layout. See 'protected/views/layouts/column2.php'.
     */
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
     * Delete category id.
     * @param <type> $category_id category id.
     */
    public function actionDeleteCategory($category_id = null) {
        if ($category_id === null) {
            throw new CHttpException(404, 'The requested page does not exist.');
        }
        $items = Item::model()->findAll('category_id =:categoryID', array(
            ':categoryID' => $category_id,
        ));
        if (count($items) > 0 ) {
            foreach($items as $item) {
                $item->delete();
            }
        }
        $this->loadModel($category_id)->delete();

        echo 'success';
    }

    /**
     * update category id.
     * @param <type> $id category_id
     */
    public function actionAjaxUpdateCategory($id = null) {
        if ($id !== null) {
            $model = $this->loadModel($id);
        } else {
            $model = new Category;
        }

        // Uncomment the following line if AJAX validation is needed
        $this->performAjaxValidation($model);

        if (isset($_POST['Category'])) {
            $model->attributes = $_POST['Category'];
            if ($model->save()) {
                Yii::app()->user->setFlash('categoryUpdated', 'Category Updated!!!!');
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