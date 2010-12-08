<?php

return CMap::mergeArray(
        require(dirname(__FILE__) . '/main.php'),
        array(
            'components' => array(
                'fixture' => array(
                    'class' => 'system.test.CDbFixtureManager',
                ),
                'db' => array(
                    'connectionString' => 'mysql:host=localhost;dbname=charashop_test',
                    'emulatePrepare' => true,
                    'username' => 'root',
                    'password' => 'root',
                    'charset' => 'utf8',
                ),
                'log' => array(
                    'class' => 'CLogRouter',
                    'routes' => array(
                        array(
                            'class' => 'CFileLogRoute',
                            'levels' => 'trace',
                            'logPath' => dirname(__FILE__) . '/../log/',
                            'logFile' => 'log-test.txt',
                            'maxFileSize' => 4096,
                            'maxLogFiles' => 4,
                        ),
                    // uncomment the following to show log messages on web pages
                    /*
                      array(
                      'class'=>'CWebLogRoute',
                      ),
                     */
                    ),
                ),
            ),
        )
);
