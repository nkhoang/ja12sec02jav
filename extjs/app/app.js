Ext.Loader.setConfig({
    enabled:true,
    paths:{
    }
});

Ext.application({
    // You need a budget
    name:'YNAB',
    appFolder:YANB.utils.buildURL('ui/extjs/app'),
    controllers:[  ],
    autoCreateViewport:true,
    launch:function () {
        Ext.History.init();
    }
});
