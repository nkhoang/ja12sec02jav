Ext.define('practView.view.practitioner.detailsViewPanel', {
  extend: 'Ext.panel.Panel',
  alias: 'widget.detailsViewPanel',
  title: 'Details',

  practDetailTplMarkup:[
    '<b>Name:</b> {name}<br />',
    '<b>NPI:</b> {npi}<br />',
    '<b>Type:</b> {type}<br />',
    '<b>Address:</b> {address}<br />',
    '<b>Status:</b> {status}<br />',
    '<b>Vendible:</b> {vendible}<br />',
    '<b>Number of Affiliations:</b> {numAffil}<br />'
  ],
  dockedItems: [{
    xtype: 'toolbar',
    dock: 'bottom',
    ui: 'footer',
    defaults: {},
    items: [
      { xtype: 'button',
        id: 'detailViewBackBtn',
        text: 'back' }
    ]
  }],

  initComponent: function() {
    var me = this;
    this.tpl = Ext.create('Ext.Template', this.practDetailTplMarkup);

    this.callParent(arguments);
  },

  updateDetail: function(data) {
    this.tpl.overwrite(this.body, data);
  }
});