Ext.Loader.setConfig({
    enabled:true,
    paths:{
        'dynamic':'http://localhost:8888/component/'
    }
});

Ext.application({
    name:'dynamicLoad',

    appFolder:'dynamicLoad',

    controllers:[
        'DynamicLoad'
    ],

    launch:function () {
        Ext.create('Ext.container.Viewport', {
            layout:'fit',
            renderTo:'page-content',
            items:[

            ]
        });
    }
});