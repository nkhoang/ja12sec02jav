Ext.define('practView.view.grid.childGrid', {
   extend:'Ext.grid.Panel',
   requires:['practView.store.nestedGridStore'],
   alias:'widget.childGrid',

   columns:[
      { header:'Name', dataIndex:'name' },
      { header:'Senority', dataIndex:'senority' }
   ],
   height:100,
   width:300,

   initComponent:function () {
      var me = this;

      Ext.applyIf(me, {
         store:'nestedGridStore'
      })

      this.callParent(arguments);
   }
})