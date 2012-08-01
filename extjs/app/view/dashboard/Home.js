Ext.define('YNAB.view.dashboard.Home', {
    extend:'Ext.panel.Panel',

    frame:true,
    layout:{
        type:'border'
    },

    initComponent:function () {
        var me = this;

        Ext.applyIf(me, {
            items:[]
        });

        me.callParent(arguments);
    }
});
