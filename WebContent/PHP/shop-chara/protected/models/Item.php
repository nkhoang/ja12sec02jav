<?php

/**
 * This is the model class for table "item".
 *
 * The followings are the available columns in table 'item':
 * @property integer $id
 * @property string $description
 * @property string $item_id
 * @property string $price
 * @property integer $quantity
 * @property integer $is_hot
 * @property integer $is_discounting
 * @property string $last_update
 * @property string $first_added
 *
 * The followings are the available model relations:
 * @property ItemPicture[] $itemPictures
 */
class Item extends CActiveRecord {

    public $number_part;
    public $category_prefix;

    /**
     * Returns the static model of the specified AR class.
     * @return Item the static model class
     */
    public static function model($className=__CLASS__) {
        return parent::model($className);
    }

    /**
     * @return string the associated database table name
     */
    public function tableName() {
        return 'item';
    }

    /**
     * @return array validation rules for model attributes.
     */
    public function rules() {
// NOTE: you should only define rules for those attributes that
// will receive user inputs.
        return array(
            array('price, quantity, number_part, weight', 'required'),
            array('price, quantity, number_part, weight', 'numerical', 'integerOnly' => true),
            array('item_id', 'length', 'max' => 256),
            array('price', 'length', 'max' => 3, 'min' => 2),
            array('weight', 'length', 'min' => 2, 'max' => 4),
            array('quantity', 'length', 'min' => 1, 'max' => 2),
            array('number_part', 'length', 'max' => 5),
            array('category_prefix', 'checkCategoryCode'),
            array('number_part', 'checkNextNumber'),
            array('item_id', 'unique'),
            array('item_id, weight, description, last_update, first_added, is_hot, is_discounting, category_id, category_prefix, number_part', 'safe'),
            // The following rule is used by search().
            // Please remove those attributes that should not be searched.
            array('id, item_id, price, quantity, is_hot, is_discounting, last_update, first_added', 'safe', 'on' => 'search'),
        );
    }

    /**
     * Check category code and make sure that it have the same code value as Category list.
     * @param <type> $attribute Not in use.
     * @param <type> $params Not in use.
     */
    public function checkCategoryCode($attribute, $params) {
        $category = Category::model()->findByPk($this->category_id);
        if ($category->category_code !== $this->category_prefix) {
            $this->addError('category_prefix', 'Category Code and Category prefix must be match.');
        }
    }

    /**
     * Make sure that it can detect item in editing mode or creation mode.
     * If in creation mode it will allow the number = next available number only
     * otherwise it must allow number equals to current number  and recommend the next available number.
     * @param <type> $attribute Not in use.
     * @param <type> $params Not in use.
     */
    public function checkNextNumber($attribute, $params) {
        $nextNumber = Category::getNextItemNumberByCategoryCode($this->category_prefix);
        if (isset($this->id)) {
            $item = Item::model()->findByPk($this->id); // load item to get the old value
            $old_category = Category::model()->findByPk($item->category_id);
            $old_number_part = substr($item->item_id, 2);
            if ($this->number_part !== $old_number_part && $this->number_part !== $nextNumber) {
                $this->addError('number_part', 'Next number should be.' . $this->category_prefix . ':' . $nextNumber . ' or the same of its old number ' . $old_category->category_code . ':' . $old_number_part);
            }
        } else {
            // get next number
            if ($this->number_part !== $nextNumber) {
                $this->addError('number_part', 'Next number should be.' . $nextNumber);
            }
        }
    }

    /**
     * @return array relational rules.
     */
    public function relations() {
// NOTE: you may need to adjust the relation name and the related
// class name for the relations automatically generated below.
        return array(
            'itemPictures' => array(self::HAS_MANY, 'ItemPicture', 'item_id'),
            'category' => array(self::BELONGS_TO, 'Category', 'category_id'),
        );
    }

    /**
     * @return array customized attribute labels (name=>label)
     */
    public function attributeLabels() {
        return array(
            'id' => 'ID',
            'description' => 'Description',
            'item_id' => 'Item',
            'price' => 'Price',
            'quantity' => 'Quantity',
            'is_hot' => 'Hot',
            'is_discounting' => 'Discount',
            'last_update' => 'Last Update',
            'first_added' => 'First Added',
            'category_id' => 'Category',
            'weight' => 'Weight',
        );
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
        $criteria->compare('item_id', $this->item_id, true);
        $criteria->compare('price', $this->price, true);
        $criteria->compare('quantity', $this->quantity);
        $criteria->compare('is_hot', $this->is_hot);
        $criteria->compare('is_discounting', $this->is_discounting);
        $criteria->compare('last_update', $this->last_update, true);
        $criteria->compare('first_added', $this->first_added, true);

        return new CActiveDataProvider(get_class($this), array(
            'criteria' => $criteria,
        ));
    }

    /*
     * Return the last item of a specific category.
     */

    public function lastItem($categoryID) {
        $this->getDbCriteria()->mergeWith(array(
            'condition' => 'category_id=:categoryID',
            'params' => array(
                ':categoryID' => $categoryID,
            ),
            'order' => 'item_id desc',
            'limit' => 1,
        ));
        return $this;
    }

    public function hotStuff($limit=5) {
        $this->getDbCriteria()->mergeWith(array(
            'condition' => 'is_hot=1',
            'limit' => $limit,
        ));
        return $this;
    }

    public function scopes() {
        return array(
            'onSales' => array(
                'condition' => 'is_discounting=1',
            ),
        );
    }

}