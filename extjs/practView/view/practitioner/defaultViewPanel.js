Ext.define('practView.view.practitioner.defaultViewPanel', {
  extend: 'Ext.panel.Panel',
  alias : 'widget.defaultViewPanel',
  layout: 'border',

  initComponent: function () {
    var me = this;
    this.flex = 1;
    this.items = [
      {
        xtype   : 'practDetailedPanel',
        minWidth: 250,
        region  : 'west',
        flex    : 1
      },
      {
        xtype : 'practGrid',
        height: 400,
        region: 'center',
        flex  : 3
      }
    ];

    this.callParent(arguments);
  }


})