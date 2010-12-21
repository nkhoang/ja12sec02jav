<?php

// uncomment the following to define a path alias
// Yii::setPathOfAlias('local','path/to/local-folder');
// This is the main Web application configuration. Any writable
// CWebApplication properties can be configured here.
return array(
    'basePath' => dirname(__FILE__) . DIRECTORY_SEPARATOR . '..',
    'name' => 'Chara Shop',
    // preloading 'log' component
    'preload' => array('log'),
    // autoloading model and component classes
    'import' => array(
        'application.models.*',
        'application.components.*',
    ),
    'modules' => array(
        // uncomment the following to enable the Gii tool
        'authorization' => array(
            'superUser' => 'admin', // change this if you wish to use a different super user name
        ),
        'gii' => array(
            'class' => 'system.gii.GiiModule',
            'password' => 'admin',
        ),
    ),
    // application components
    'components' => array(
        'user' => array(
            // enable cookie-based authentication
            'allowAutoLogin' => true,
        ),
        'authManager' => array(
            'class' => 'CDbAuthManager',
            'connectionID' => 'db',
        ),
        // uncomment the following to enable URLs in path-format

        'urlManager' => array(
            'urlFormat' => 'path',
            'showScriptName' => false,
            'urlSuffix' => '.html',
            'rules' => array(
                '<_c:(user)>/<_a:(delete|update|create)>/<id:\d+>' => '_c/_a',
            ),
        ),
        /*
          'db'=>array(
          'connectionString' => 'sqlite:'.dirname(__FILE__).'/../data/testdrive.db',
          ),
         */

        'db' => array(
            'connectionString' => 'mysql:host=localhost;dbname=charashop_dev',
            'emulatePrepare' => true,
            'username' => 'root',
            'password' => 'root',
            'charset' => 'utf8',
        ),
        'errorHandler' => array(
            // use 'site/error' action to display errors
            'errorAction' => 'site/error',
        ),
        'log' => array(
            'class' => 'CLogRouter',
            'routes' => array(
                array(
                    'class' => 'CFileLogRoute',
                    'levels' => 'error, info',
                    'logPath' => dirname(__FILE__) . '/../log/',
                    'logFile' => 'log.txt',
                    'maxFileSize' => 4096,
                    'maxLogFiles' => 4,
                ),
                array(
                    //'categories' => 'debug, yii',
                    'class' => 'CWebLogRoute',
                    'enabled' => true,
                    'showInFireBug' => true,
                    'ignoreAjaxInFireBug' => false,
                    'levels' => 'info, warning, error, debug',
                ),
                // the FirePHP LogRoute
                
            // uncomment the following to show log messages on web pages
            /*
              array(
              'class'=>'CWebLogRoute',
              ),
             */
            ),
        ),
    ),
    // application-level parameters that can be accessed
    // using Yii::app()->params['paramName']
    'params' => array(
        // this is used in contact page
        'adminEmail' => 'webmaster@example.com',
    ),
);