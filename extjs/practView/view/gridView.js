Ext.define('practView.view.gridView', {
   extend:'Ext.panel.Panel',
   requires:['practView.view.grid.nestedGrid', 'practView.view.grid.childGrid'],

   alias:'widget.gridView',
   layout:'anchor',
   height:600,
   defaults:{
      margin:'10px',
      height: 600,
      draggable:true
   },
   initComponent:function () {
      var me = this;

      me.items = [
         {
            xtype:'nestedGrid'
         }
      ];

      this.callParent(arguments);
   }
})