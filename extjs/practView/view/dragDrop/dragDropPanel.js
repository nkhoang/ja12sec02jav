Ext.define('practView.view.dragDrop.dragDropPanel', {
   extend: 'Ext.panel.Panel',
   alias: 'widget.dragDropPanel',
   height: 200,

   initComponent: function() {
      var me = this;

      me.addEvents('panelOnDrag', 'otherPanelOnDrag');

      me.draggable = {
         insertProxy: false,
         onDrag: function(e) {
            console.debug("Dragging...");
            me.fireEvent("panelOnDrag", me, e);
         }

      };

      this.callParent(arguments);
   }
})