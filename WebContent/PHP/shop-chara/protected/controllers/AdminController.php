<?php

class AdminController extends Controller {

    public function actionAdmin() {
        $this->render('admin');
    }

    public function actionListTasks() {
        if (Yii::app()->request->isAjaxRequest) { // accept ajax request only
            $roleID = '';
            if (isset($_POST['roleID'])) {
                $roleID = $_POST['roleID']; // dont' have to convert because it is string value.

                $authChildList = Authitemchild::model()->findAll(array(
                            'condition' => 'parent=:roleID',
                            'params' => array(
                                'roleID' => $roleID,
                            ),
                        ));

                $taskData = CHtml::listData($authChildList, 'child', 'child');
            }
            $this->renderPartial('index', array(
                'taskList' => $taskData
            ));
        }
    }

    private function checkItemIsParent($item) {
        $item = Authitem::model()->findByPk($item->child);
        $result = false;
        if (isset($item)) {
            if ($item->type === 2) {
                $result = true;
            }
        }

        return $result;
    }

    public function actionIndex() {
        $roles = Authitem::model()->findAll(array(
                    'condition' => 'type=:roleType',
                    'params' => array(
                        ':roleType' => '2'
                    ),
                ));

        $roleData = CHtml::listData($roles, 'name', 'name');



        $this->render('index', array(
            'roleList' => $roleData,
            'taskList' => array(),
        ));
    }

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
            array('allow', // allow all users to perform 'index' and 'view' actions
                'actions' => array('index', 'admin', 'listTasks'),
                'users' => array('@'),
            ),
            array('deny', // deny all users
                'users' => array('*'),
            ),
        );
    }

}