Ext.define('YNAB.view.Viewport', {
    extend:'Ext.container.Viewport',
    id:'viewport',

    requires:[
        'YNAB.view.dashboard.Home'
    ],

    layout:{
        type:'fit'
    },

    initComponent:function () {
        var me = this;

        Ext.applyIf(me, {
            items:[
                {
                    xtype:'panel',
                    title:'Just to have something'
                }
            ]
        });

        me.callParent(arguments);
    }

});