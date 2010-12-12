<?php
/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
return array(
    'autheitemchild1' => array(
        'parent' => 'user',
        'child' => 'createItem',
    ),
    'autheitemchild2' => array(
        'parent' => 'admin',
        'child' => 'createUser',
    ),
    'autheitemchild3' => array(
        'parent' => 'admin',
        'child' => 'deleteItem',
    ),
    'autheitemchild4' => array(
        'parent' => 'admin',
        'child' => 'deleteUser',
    ),
    'autheitemchild5' => array(
        'parent' => 'admin',
        'child' => 'guest',
    ),
    'autheitemchild6' => array(
        'parent' => 'user',
        'child' => 'guest',
    ),
    'autheitemchild7' => array(
        'parent' => 'user',
        'child' => 'updateItem',
    ),
    'autheitemchild8' => array(
        'parent' => 'user',
        'child' => 'updateUser',
    ),
    'autheitemchild9' => array(
        'parent' => 'admin',
        'child' => 'user',
    ),
    'autheitemchild10' => array(
        'parent' => 'guest',
        'child' => 'viewItem',
    ),
    'autheitemchild11' => array(
        'parent' => 'user',
        'child' => 'viewUser',
    ),
    

);
?>
