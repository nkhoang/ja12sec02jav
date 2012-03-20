Ext.define('practView.view.grid.nestedGrid', {
   extend:'Ext.grid.Panel',
   requires:['practView.store.parentGridStore'],
   alias:'widget.nestedGrid',

   columns:[
      { header:'First Name', dataIndex:'firstName' },
      { header:'Last Name', dataIndex:'lastName', flex:1 },
      { header:'Email', dataIndex:'email' },
      { header:'Last Login', dataIndex:'lastLogin' },
      { header:'Disabled', dataIndex:'disabled' }
   ],
   height:200,
   width:400,
   scroll:false,
   viewConfig:{
      style:{ overflow:'auto', overflowX:'hidden' }
   },
   plugins:[
      {
         ptype:'rowexpander',
         rowBodyTpl:'<div class="childGrid" style="background-color: white">Hi this is child grid</div>',
         expandOnDblClick: false
      }
   ],

   initComponent:function () {
      var me = this;

      Ext.applyIf(me, {
         store:'parentGridStore'
      })

      this.callParent(arguments);
   }
})