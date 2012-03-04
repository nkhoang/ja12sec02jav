Ext.define('practView.view.dragDropView', {
   extend:'Ext.panel.Panel',
   requires: ['practView.view.dragDrop.dragDropPanel'],
   alias:'widget.dragDropView',
   defaults:{
      margin:'10px',
      height:200,
      draggable:true
   },
   initComponent:function () {
      var me = this;

      me.items = [
         {
            xtype:'dragDropPanel',
            id: 'Panel-1',
            title:'Panel 1'
         },
         {
            xtype:'dragDropPanel',
            id: 'Panel-2',
            title:'Panel 2'
         },
         {
            xtype:'dragDropPanel',
            id: 'Panel-3',
            title:'Panel 3'
         },
         {
            xtype:'dragDropPanel',
            id: 'Panel-4',
            title:'Panel 4'
         }
      ];

      this.callParent(arguments);
   },

   updateDetail:function (data) {
      console.debug('Updating detail...');
      console.debug(data);
      this.tpl.overwrite(this.body, data);
      this.doLayout();
   },
   processEventQueue:function () {
      console.debug('Process event queue with size: [' + this.eventQueue.length + ']');
      do {
         var event = this.eventQueue.pop();
         event.fn.apply(this, event.args);
      }
      while (this.eventQueue.length != 0)
   }
})