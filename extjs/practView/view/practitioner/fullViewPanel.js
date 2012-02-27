Ext.define('practView.view.practitioner.fullViewPanel', {
  extend: 'Ext.panel.Panel',
  alias: 'practFullViewPanel',
  title: 'abc',

  practDetailTplMarkup:[
    '<b>Name:</b> {name}<br />',
    '<b>NPI:</b> {npi}<br />',
    '<b>Type:</b> {type}<br />',
    '<b>Address:</b> {address}<br />',
    '<b>Status:</b> {status}<br />',
    '<b>Vendible:</b> {vendible}<br />',
    '<b>Number of Affiliations:</b> {numAffil}<br />'
  ],

  initComponent: function() {
    this.tpl = Ext.create('Ext.Template', this.practDetailTplMarkup);
  },

  updateDetail: function(data) {
    this.tpl.overwrite(this.body, data);

  }
});