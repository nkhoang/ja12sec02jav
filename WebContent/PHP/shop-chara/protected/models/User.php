<?php

/**
 * This is the model class for table "user".
 *
 * The followings are the available columns in table 'user':
 * @property integer $id
 * @property string $first_name
 * @property string $middle_name
 * @property string $last_name
 * @property string $email
 * @property string $username
 * @property string $password
 * @property string $create_time
 * @property string $update_time
 * @property string $last_login_time
 * @property integer $account_locked
 */
class User extends CActiveRecord {

    public $password_confirm;

    /**
     * Returns the static model of the specified AR class.
     * @return User the static model class
     */
    public static function model($className=__CLASS__) {
        return parent::model($className);
    }

    /**
     * @return string the associated database table name
     */
    public function tableName() {
        return 'user';
    }

    /**
     * @return array validation rules for model attributes.
     */
    public function rules() {
        // NOTE: you should only define rules for those attributes that
        // will receive user inputs.
        return array(
            array('first_name, last_name, email, username, password_confirm', 'required'),
            array('first_name, middle_name, last_name, email, password', 'length', 'max' => 256),
            array('email, username', 'unique'),
            array('username', 'length', 'max' => 50),
            array('password', 'compare', 'compareAttribute' => 'password_confirm'),
            // The following rule is used by search().
            // Please remove those attributes that should not be searched.
            array('id, first_name, last_name, email, username', 'safe', 'on' => 'search'),
            array('password_confirm', 'safe'),
        );
    }

    /**
     * @return array relational rules.
     */
    public function relations() {
        // NOTE: you may need to adjust the relation name and the related
        // class name for the relations automatically generated below.
        return array(
        );
    }

    /**
     * @return array customized attribute labels (name=>label)
     */
    public function attributeLabels() {
        return array(
            'id' => 'ID',
            'first_name' => 'First Name',
            'middle_name' => 'Middle Name',
            'last_name' => 'Last Name',
            'email' => 'Email',
            'username' => 'Username',
            'password' => 'Password',
            'password_confirm' => 'Confirm Password',
            'create_time' => 'Create Time',
            'update_time' => 'Update Time',
            'last_login_time' => 'Last Login Time',
            'account_locked' => 'Account Locked',
            'roles' => 'Roles',
        );
    }

    /**
     * Get user current roles.
     * @return string
     */
    public function getRoles() {
        $rolesStr = '';

        $userRoles = Authassignment::model()->findAll(array(
                    'condition' => 'userid=:userID',
                    'params' => array(':userID' => $this->id),
                ));
        if ($userRoles !== null && sizeof($userRoles) > 0) {
            $count = 0;
            foreach ($userRoles as $userRole) {
                $count++;
                $rolesStr = $rolesStr . $userRole->itemname;
                if ($count != sizeof($userRoles)) {
                    $rolesStr = $rolesStr . ' ,';
                }
            }
        }


        return $rolesStr;
    }

    /**
     * Get available roles in system.
     * @return <type>
     */
    public function getAvailableRoles() {
        if (Yii::app()->user->checkAccess('admin')) {
            $result = null;
            $userRoles = Authitem::model()->findAll(array(
                        'condition' => 'type=:typeID',
                        'params' => array(':typeID' => 2),
                    ));
            if ($userRoles !== null && sizeof($userRoles) > 0) {
                $result = CHtml::listData($userRoles, 'name', 'name');
            }
            return $result;
        }
    }

    /**
     * Retrieves a list of models based on the current search/filter conditions.
     * @return CActiveDataProvider the data provider that can return the models based on the search/filter conditions.
     */
    public function search() {
        // Warning: Please modify the following code to remove attributes that
        // should not be searched.

        $criteria = new CDbCriteria;

        $criteria->compare('id', $this->id);
        $criteria->compare('first_name', $this->first_name, true);
        $criteria->compare('middle_name', $this->middle_name, true);
        $criteria->compare('last_name', $this->last_name, true);
        $criteria->compare('email', $this->email, true);
        $criteria->compare('username', $this->username, true);
        $criteria->compare('password', $this->password, true);
        $criteria->compare('create_time', $this->create_time, true);
        $criteria->compare('update_time', $this->update_time, true);
        $criteria->compare('last_login_time', $this->last_login_time, true);
        $criteria->compare('account_locked', $this->account_locked);

        return new CActiveDataProvider(get_class($this), array(
            'criteria' => $criteria,
        ));
    }

    public function afterValidate() {
        parent::afterValidate();
        $this->password = md5($this->password);
    }

}